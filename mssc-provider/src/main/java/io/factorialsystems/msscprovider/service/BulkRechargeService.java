package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.RechargeMapper;
import io.factorialsystems.msscprovider.domain.*;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.mapper.recharge.BulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkRechargeService {
    private final JmsTemplate jmsTemplate;
    private final FactoryProducer producer;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RechargeMapper rechargeMapper;
    private final BulkRechargeMapper bulkRechargeMapper;
    private final BulkRechargeMapstructMapper rechargeMapstructMapper;

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    public void asyncBulkRecharge(String id) {

        BulkRechargeRequest request = bulkRechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            final String errMessage = String.format("Unable to Load Request from database payment or request is closed id (%s)", id);
            log.error(errMessage);
            throw new RuntimeException(errMessage);
        }

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            final String message = String.format("Payment Not found or No Payment has been made for Bulk Recharge (%s)", request.getId());
            log.error(message);
            throw new RuntimeException(message);
        }

        try {
            log.info(String.format("AsyncBulk Recharge: Sending message for Asynchronous processing of Bulk Recharge Request (%s)", id));
            jmsTemplate.convertAndSend(JMSConfig.BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(id));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public BulkRechargeRequestResponseDto saveService(BulkRechargeRequestDto dto) {

        if (dto.getRecipients() == null && dto.getGroupId() == null) {
            log.error("No Group or Recipients specified, nothing todo");

            return BulkRechargeRequestResponseDto.builder()
                    .status(300)
                    .message("No Group or Recipients specified, nothing todo")
                    .build();
        }

        BulkRechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);

        PaymentRequestDto paymentRequestDto = initializePayment (request);
        paymentRequestDto.setPaymentMode(request.getPaymentMode());

        if (paymentRequestDto.getPaymentMode().equals("wallet") && paymentRequestDto.getStatus() != 200) {
            return BulkRechargeRequestResponseDto.builder()
                    .status(paymentRequestDto.getStatus())
                    .message(paymentRequestDto.getMessage())
                    .build();
        }

        request.setPaymentId(paymentRequestDto.getId());

        String requestId = UUID.randomUUID().toString();
        request.setId(requestId);
        bulkRechargeMapper.save(request);

        if (dto.getRecipients() != null && dto.getRecipients().length > 0) {
            List<BulkRecipient> recipients = new ArrayList<>(dto.getRecipients().length);
            Arrays.stream(dto.getRecipients()).forEach( recipient -> {
                recipients.add(new BulkRecipient(requestId, recipient));
            });

            bulkRechargeMapper.saveRecipients(recipients);
        }

        if (request.getPaymentMode().equals("wallet")) {
            saveTransaction(request);

            try {
                log.info(String.format("Sending message for Asynchronous processing of Bulk Recharge Request (%s)", requestId));
                jmsTemplate.convertAndSend(JMSConfig.BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(requestId));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return BulkRechargeRequestResponseDto.builder()
                .id(requestId)
                .message("Success")
                .status(200)
                .paymentMode(request.getPaymentMode())
                .redirectUrl(paymentRequestDto.getRedirectUrl())
                .authorizationUrl(paymentRequestDto.getAuthorizationUrl())
                .build();
    }

    public void runBulkRecharge(String id) {

        log.info(String.format("received message for Asynchronous processing of Bulk Recharge Request (%s)", id));

        BulkRechargeRequest request = bulkRechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            final String errMessage = String.format("Unable to Load Request from database payment or request is closed id (%s)", id);
            log.error(errMessage);
            return;
        }

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            final String message = String.format("Payment Not found or No Payment has been made for Bulk Recharge (%s)", request.getId());
            log.error(message);
            return;
        }

        List<RechargeFactoryParameters> parameters = rechargeMapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());

            // Use BulkRecipient Class and remove Telephone class not necessary
            List<Telephone> telephones =  bulkRechargeMapper.findRecipients(id);
            telephones.forEach(telephone -> {
                recharge.recharge(
                        SingleRechargeRequest.builder()
                                .serviceId(request.getServiceId())
                                .serviceCode(request.getServiceCode())
                                .serviceCost(request.getServiceCost())
                                .id(id)
                                .recipient(telephone.getMsisdn())
                                .build()
                );
            });

            if (request.getGroupId() != null) {
                List<BeneficiaryDto> beneficiaries = restTemplate.getForObject(baseLocalUrl + "/api/v1/beneficiary/beneficiary/" + request.getGroupId(), List.class);

                if (beneficiaries != null) {

                    beneficiaries.forEach(beneficiary -> {
                        recharge.recharge(
                                SingleRechargeRequest.builder()
                                        .serviceId(request.getServiceId())
                                        .serviceCode(request.getServiceCode())
                                        .serviceCost(request.getServiceCost())
                                        .id(id)
                                        .recipient(beneficiary.getTelephone())
                                        .build()
                        );
                    });
                }
            }

            if (request.getPaymentMode().equals("paystack")) {
                saveTransaction(request);
            }

            bulkRechargeMapper.closeRequest(id);
        }
    }

    private PaymentRequestDto initializePayment(BulkRechargeRequest request) {

        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getTotalServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .paymentMode(request.getPaymentMode())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return restTemplate.postForObject(baseLocalUrl + "api/v1/payment", dto, PaymentRequestDto.class);
    }

    private void saveTransaction(BulkRechargeRequest request) {

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(request.getServiceId())
                .requestId(request.getId())
                .serviceCost(request.getTotalServiceCost())
                .transactionDate(new Date().toString())
                .userId(K.getUserId())
                .build();
        try {
            jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
        } catch (JsonProcessingException e) {
            log.error("Error sending JMS Transaction Message to Wallet service {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private Boolean checkPayment(String id) {
        PaymentRequestDto dto
                = restTemplate.getForObject(baseLocalUrl + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }
}
