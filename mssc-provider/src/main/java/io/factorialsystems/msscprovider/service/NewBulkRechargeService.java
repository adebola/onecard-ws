package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.mapper.recharge.NewBulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.service.model.ServiceHelper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewBulkRechargeService {
    private final ServiceHelper helper;
    private final JmsTemplate jmsTemplate;
    private final FactoryProducer producer;
    private final ObjectMapper objectMapper;
    private final FileUploader fileUploader;
    private final NewBulkRechargeMapstructMapper mapper;
    private final SingleRechargeMapper singleRechargeMapper;
    private final NewBulkRechargeMapper newBulkRechargeMapper;

    public void uploadRecharge(MultipartFile file) {
        UploadFile uploadFile = fileUploader.uploadFile(file);
        ExcelReader excelReader = new ExcelReader(uploadFile);

        NewBulkRechargeRequestDto newRequestDto = new NewBulkRechargeRequestDto();
        newRequestDto.setPaymentMode("wallet");
        List<IndividualRequestDto> individualRequests = excelReader.readContents();

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new RuntimeException("Empty or Un-Populated Excel file");
        }

        newRequestDto.setRecipients(individualRequests);
        saveService(newRequestDto);
    }

    @Transactional
    public BulkRechargeResponseDto saveService(NewBulkRechargeRequestDto dto) {

        if (dto.getRecipients() == null || dto.getRecipients().isEmpty()) {
            log.error("No Group or Recipients specified, nothing todo");

            return BulkRechargeResponseDto.builder()
                    .status(300)
                    .message("No Group or Recipients specified, nothing todo")
                    .build();
        }

        NewBulkRechargeRequest request = mapper.rechargeDtoToRecharge(dto);
        PaymentRequestDto requestDto = helper.initializePayment(request);

        if (requestDto.getPaymentMode().equals("wallet") && requestDto.getStatus() != 200) {
            return BulkRechargeResponseDto.builder()
                    .status(requestDto.getStatus())
                    .message("Payment Failure: " + requestDto.getMessage())
                    .totalCost(request.getTotalServiceCost())
                    .build();
        }

        request.setPaymentId(requestDto.getId());
        request.setAuthorizationUrl(requestDto.getAuthorizationUrl());
        request.setRedirectUrl(requestDto.getRedirectUrl());

        final String requestId = UUID.randomUUID().toString();
        request.setId(requestId);

        newBulkRechargeMapper.saveBulkRecharge(request);
        request.getRecipients().forEach(i -> {
            i.setBulkRequestId(requestId);
        });
        newBulkRechargeMapper.saveBulkIndividualRequests(request.getRecipients());

        if (request.getPaymentMode().equals("wallet")) {
            saveTransaction(request);

            try {
                log.info(String.format("Sending message for Asynchronous processing of Bulk Recharge Request (%s)", requestId));
                jmsTemplate.convertAndSend(JMSConfig.NEW_BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(requestId));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return BulkRechargeResponseDto.builder()
                .id(requestId)
                .message("Bulk Recharge Request Submitted Successfully")
                .status(200)
                .totalCost(request.getTotalServiceCost())
                .paymentMode(request.getPaymentMode())
                .redirectUrl(request.getRedirectUrl())
                .authorizationUrl(request.getAuthorizationUrl())
                .build();
    }

    public void runBulkRecharge(String id) {
        log.info(String.format("received message for processing of New Bulk Recharge Request (%s)", id));
        NewBulkRechargeRequest request = newBulkRechargeMapper.findBulkRechargeById(id);

        if (request == null || request.getClosed()) {
            final String errMessage = String.format("Unable to Load Request from database payment or request is closed id (%s)", id);
            log.error(errMessage);
            return;
        }

        newBulkRechargeMapper.closeRequest(id);

        // We don't need to check for payment if it is a Scheduled Recharge as Payment is made before Scheduling
        // The Payment is also checked before invoking this function.
        if (request.getScheduledRequestId() == null) {
            if (request.getPaymentId() != null && !helper.checkPayment(request.getPaymentId())) {
                final String message = String.format("Payment Not found or No Payment has been made for Bulk Recharge (%s)", request.getId());
                log.error(message);
                return;
            }
        }

        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequests(id);

        if (individualRequests == null || individualRequests.isEmpty()) {
            log.error("Individual Bulk Requests not found for Bulk Request {}", id);
            return;
        }

        individualRequests.forEach(individualRequest -> {
            List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(individualRequest.getServiceId());

            if (parameters != null && !parameters.isEmpty()) {
                RechargeFactoryParameters parameter = parameters.get(0);
                String rechargeProviderCode = parameter.getRechargeProviderCode();
                AbstractFactory factory = producer.getFactory(rechargeProviderCode);
                String serviceAction = parameter.getServiceAction();

                SingleRechargeRequest singleRechargeRequest = SingleRechargeRequest.builder()
                        .serviceId(individualRequest.getServiceId())
                        .serviceCode(individualRequest.getServiceCode())
                        .serviceCost(individualRequest.getServiceCost())
                        .id(UUID.randomUUID().toString())
                        .recipient(individualRequest.getRecipient())
                        .productId(individualRequest.getProductId())
                        .build();

               ParameterCheck parameterCheck = factory.getCheck(serviceAction);

                if (parameterCheck.check(singleRechargeRequest)) {
                    Recharge recharge = factory.getRecharge(parameter.getServiceAction());
                    recharge.recharge(singleRechargeRequest);
                } else {
                    final String errorMessage =
                            String.format("Invalid Parameters in One of the Requests in a BulkRecharge Action: (%s) serviceCode: (%s), Recipient: (%s) Made By: (%s)",
                                    serviceAction, individualRequest.getServiceCode(), individualRequest.getRecipient(), K.getEmail());
                    log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }
        });

        if (request.getPaymentMode().equals("paystack") && request.getScheduledRequestId() == null) {
            saveTransaction(request);
        }
    }

    public void asyncRecharge(String id) {
        NewBulkRechargeRequest request = newBulkRechargeMapper.findBulkRechargeById(id);

        if (request == null || request.getClosed()) {
            final String errMessage = String.format("Unable to Load Request from database payment or request is closed id (%s)", id);
            log.error(errMessage);
            throw new RuntimeException(errMessage);
        }

        if (request.getPaymentId() != null && !helper.checkPayment(request.getPaymentId())) {
            final String message = String.format("Payment Not found or No Payment has been made for Bulk Recharge (%s)", request.getId());
            log.error(message);
            throw new RuntimeException(message);
        }

        try {
            log.info(String.format("AsyncBulk Recharge: Sending message for Asynchronous processing of Bulk Recharge Request (%s)", id));
            jmsTemplate.convertAndSend(JMSConfig.NEW_BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(id));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveTransaction(NewBulkRechargeRequest request) {

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .requestId(request.getId())
                .serviceCost(request.getTotalServiceCost())
                .transactionDate(new Date().toString())
                .userId(request.getUserId())
                .recipient("Bulk")
                .build();
        try {
            jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
        } catch (JsonProcessingException e) {
            log.error("Error sending JMS Transaction Message to Wallet service {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
