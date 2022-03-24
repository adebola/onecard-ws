package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.ScheduledRechargeMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.BulkRecipient;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.BulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.mapper.recharge.BulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.mapper.recharge.ScheduledRechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledRechargeService {

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    public static final String BULK_RECHARGE = "bulk";
    public static final String SINGLE_RECHARGE = "single";
    public static final Integer SINGLE_RECHARGE_N = 1;
    public static final Integer BULK_RECHARGE_N = 2;

    private final JmsTemplate jmsTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FactoryProducer factoryProducer;
    private final BulkRechargeMapper bulkRechargeMapper;
    private final BulkRechargeService bulkRechargeService;
    private final SingleRechargeMapper singleRechargeMapper;
    private final SingleRechargeService singleRechargeService;
    private final RechargeMapstructMapper rechargeMapstructMapper;
    private final ScheduledRechargeMapper scheduledRechargeMapper;
    private final BulkRechargeMapstructMapper bulkRechargeMapstructMapper;
    private final ScheduledRechargeMapstructMapper scheduledRechargeMapstructMapper;

    public ScheduledRechargeResponseDto startRecharge(ScheduledRechargeRequestDto dto) {
        ScheduledRechargeRequest request = scheduledRechargeMapstructMapper.rechargeDtoToRecharge(dto);

        if (dto.getRechargeType().equals(SINGLE_RECHARGE)) {
            log.info("Scheduling a Single Recharge Service ScheduledDate: {} ServiceCode {}", dto.getScheduledDate().toString(), dto.getServiceCode());

            SingleRechargeRequest singleRechargeRequest = checkSingleRequestParameters(dto);

            if (request != null && singleRechargeRequest != null)  {
                PaymentRequestDto paymentRequestDto = SingleRechargeService.initializePayment(singleRechargeRequest);

                if (paymentRequestDto.getStatus() != 200) {
                    throw new RuntimeException("Payment Error / Payment Initialisation Error");
                }

                request.setStatus(paymentRequestDto.getStatus());
                request.setMessage(paymentRequestDto.getMessage());
                request.setRedirectUrl(paymentRequestDto.getRedirectUrl());
                request.setAuthorizationUrl(paymentRequestDto.getAuthorizationUrl());
                request.setPaymentId(paymentRequestDto.getId());

                // Save Request
                String id = UUID.randomUUID().toString();
                request.setId(id);
                scheduledRechargeMapper.save(request);

                if (request.getPaymentMode().equals("wallet")) {
                    saveTransaction(request, paymentRequestDto.getAmount());
                }

                return ScheduledRechargeResponseDto.builder()
                        .authorizationUrl(paymentRequestDto.getAuthorizationUrl())
                        .redirectUrl(paymentRequestDto.getRedirectUrl())
                        .paymentMode(paymentRequestDto.getPaymentMode())
                        .message(paymentRequestDto.getMessage())
                        .status(paymentRequestDto.getStatus())
                        .id(id)
                        .build();

            }
        } else if (dto.getRechargeType().equals(BULK_RECHARGE)) {
            log.info("Scheduling a Bulk Recharge Service ScheduledDate: {} ServiceCode {}", dto.getScheduledDate().toString(), dto.getServiceCode());

            if (dto.getRecipients() == null && dto.getGroupId() == null) {
                log.error("No Group or Recipients specified, nothing todo");
                return null;
            }

            BulkRechargeRequestDto bulkRechargeRequestDto = bulkRechargeMapstructMapper.scheduledToBulkRechargeDto(dto);
            BulkRechargeRequest bulkRechargeRequest  =
                    bulkRechargeMapstructMapper.rechargeDtoToRecharge(bulkRechargeRequestDto);

            PaymentRequestDto paymentRequestDto = BulkRechargeService.initializePayment (bulkRechargeRequest);
//        paymentRequestDto.setPaymentMode(request.getPaymentMode());

            if (paymentRequestDto.getPaymentMode().equals("wallet") && paymentRequestDto.getStatus() != 200) {
                final String message = String.format("Error processing Payment : (%s)", paymentRequestDto.getMessage());
                log.error(message);
                throw new RuntimeException(message);
            }

            request.setPaymentId(paymentRequestDto.getId());
            request.setStatus(paymentRequestDto.getStatus());
            request.setMessage(paymentRequestDto.getMessage());
            request.setAuthorizationUrl(paymentRequestDto.getAuthorizationUrl());
            request.setRedirectUrl(paymentRequestDto.getRedirectUrl());
            request.setTotalServiceCost(paymentRequestDto.getAmount());

            // Save Request
            String id = UUID.randomUUID().toString();
            request.setId(id);
            scheduledRechargeMapper.save(request);

            if (dto.getRecipients() != null && dto.getRecipients().length > 0) {
                List<BulkRecipient> recipients = new ArrayList<>(dto.getRecipients().length);
                Arrays.stream(dto.getRecipients()).forEach(recipient -> {
                    recipients.add(new BulkRecipient(id, recipient));
                });

                scheduledRechargeMapper.saveRecipients(recipients);
            }

            // Move SaveTransaction to the Payment Server for
            // Wallet Payment, Transaction should be created once payment goes through
            // For Paystack same
            if (request.getPaymentMode().equals("wallet")) {
                saveTransaction(request, paymentRequestDto.getAmount());
            }

            return ScheduledRechargeResponseDto.builder()
                    .authorizationUrl(paymentRequestDto.getAuthorizationUrl())
                    .redirectUrl(paymentRequestDto.getRedirectUrl())
                    .paymentMode(paymentRequestDto.getPaymentMode())
                    .message(paymentRequestDto.getMessage())
                    .status(paymentRequestDto.getStatus())
                    .id(id)
                    .build();
        } else {
            final String message =
                    String.format("Invalid Recharge Type (%s), recharge type should either be \"single\" or \"bulk\"", dto.getRechargeType());
            log.error(message);
            throw new RuntimeException(message);
        }

        return null;
    }

    public void runRecharges(List<ScheduledRechargeRequest> requests) {

        requests.forEach(request -> {
            if (Objects.equals(request.getRequestType(), SINGLE_RECHARGE_N)) {
                if (request.getPaymentId() != null && checkPayment(request.getPaymentId())) {
                    SingleRechargeRequest singleRequest = rechargeMapstructMapper.scheduleToSingleRecharge(request);

                    log.info(String.format("Processing Single Scheduled Recharge (%s)", request.getId()));

                    singleRequest.setId(UUID.randomUUID().toString());
                    singleRequest.setScheduledRequestId(request.getId());

                    if (request.getServiceCost() == null) {
                        singleRequest.setProductId(request.getProductId());
                        singleRequest.setServiceCost(getSingleRechargeServiceCost(singleRequest));
                    } else {
                        singleRequest.setServiceCost(request.getServiceCost());
                    }

                    singleRechargeMapper.save(singleRequest);
                    singleRechargeService.finishRecharge(singleRequest.getId());
                } else {
                    log.error("Skipping Scheduled Single Recharge Request {}, looks like it has not been paid for", request.getId());
                    // Log To Schedule Exception table for Internal Business Consumption
                }
                scheduledRechargeMapper.closeRequest(request.getId());
            } else if (Objects.equals(request.getRequestType(), BULK_RECHARGE_N)) {
                log.info(String.format("Processing Bulk Scheduled Recharge (%s)", request.getId()));

                BulkRechargeRequest bulkRechargeRequest = bulkRechargeMapstructMapper.scheduledToBulkRecharge(request);

                if (request.getPaymentId() != null && checkPayment(request.getPaymentId())) {
                    final String id = UUID.randomUUID().toString();

                    bulkRechargeRequest.setId(id);
                    bulkRechargeRequest.setScheduledRequestId(request.getId());
                    bulkRechargeMapper.save(bulkRechargeRequest);

                    Map<String, String> recipientMap = new HashMap<>();
                    recipientMap.put("bulkRequestId", id);
                    recipientMap.put("scheduledRequestId", request.getId());
                    bulkRechargeMapper.updateBulkRechargeId(recipientMap);

                    bulkRechargeService.runBulkRecharge(id);
                } else {
                    log.error("Skipping Scheduled Bulk Recharge Request {}, it might not be paid for", request.getId());
                }
            }

            scheduledRechargeMapper.closeRequest(request.getId());
        });

    }

    private SingleRechargeRequest checkSingleRequestParameters(ScheduledRechargeRequestDto dto) {
        SingleRechargeRequestDto singleRechargeRequestDto = rechargeMapstructMapper.scheduledToSingleRechargeDto(dto);
        SingleRechargeRequest request =
                rechargeMapstructMapper.rechargeDtoToRecharge(singleRechargeRequestDto);
        return SingleRechargeService.checkParameters(request, singleRechargeRequestDto) ? request : null;
    }

    private void saveTransaction(ScheduledRechargeRequest request, BigDecimal amount) {

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(request.getServiceId())
                .requestId(request.getId())
                .serviceCost(amount)
                .transactionDate(new Date().toString())
                .userId(request.getUserId())
                .recipient(request.getRecipient() == null ? "scheduled-bulk" : "scheduled-" + request.getRecipient())
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

    private BigDecimal getSingleRechargeServiceCost(SingleRechargeRequest request) {
        String serviceAction = null;
        String rechargeProviderCode = null;

        List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load SingleRechargeFactoryParameters for (%s)", request.getServiceCode()));
        }

        AbstractFactory factory = factoryProducer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", request.getServiceCode()));
        }

        DataEnquiry enquiry = factory.getPlans(serviceAction);
        DataPlanDto planDto = enquiry.getPlan(request.getProductId());
        return new BigDecimal(planDto.getPrice());
    }

    public Boolean finalizeScheduledRecharge(String id) {
        ScheduledRechargeRequest request = scheduledRechargeMapper.findById(id);

        if (request != null && request.getPaymentId() != null && checkPayment(request.getPaymentId())) {
            saveTransaction(request, request.getServiceCost());
            return true;
        }

        return false;
    }
}
