package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SingleRechargeService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final FactoryProducer producer;
    private final RestTemplate restTemplate;
    private final SingleRechargeMapper singleRechargeMapper;
    private final ServiceActionMapper serviceActionMapper;
    private final RechargeMapstructMapper rechargeMapstructMapper;

    public static final Integer AIRTIME_ACTION = 1;
    public static final Integer DATA_ACTION = 2;
    public static final Integer ELECTRICITY_ACTION = 3;

    private static String BASE_LOCAL_STATIC;

    @Value("${api.local.host.baseurl}")
    public void setNameStatic(String baseLocal) {
        SingleRechargeService.BASE_LOCAL_STATIC = baseLocal;
    }

    public SingleRechargeResponseDto startRecharge(SingleRechargeRequestDto dto) {
        SingleRechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);

        if (checkParameters(request, dto)) {
            PaymentRequestDto paymentRequest = initializePayment(request);

            if (paymentRequest == null) {
                throw new RuntimeException("Error Initializing Payment Please contact OneCard Support");
            }

            request.setPaymentId(paymentRequest.getId());
            request.setAuthorizationUrl(paymentRequest.getAuthorizationUrl());
            request.setRedirectUrl(paymentRequest.getRedirectUrl());
            request.setMessage(paymentRequest.getMessage());
            request.setStatus(paymentRequest.getStatus());

            request.setId(UUID.randomUUID().toString());
            log.info(String.format("Saving Recharge Request %s", request.getId()));
            singleRechargeMapper.save(request);

            if (request.getPaymentMode().equals("wallet")) {
                 if (request.getStatus() == 200) {
                     try {
                         jmsTemplate.convertAndSend(JMSConfig.SINGLE_RECHARGE_QUEUE, objectMapper.writeValueAsString(request.getId()));
                     } catch (JsonProcessingException e) {
                         log.error("Error sending Single Recharge Service to Self {}", e.getMessage());
                         throw new RuntimeException(e.getMessage());
                     }
                 } else {
                     final String message = String.format("Payment Error %s : ", request.getMessage());
                     log.error(message);
                     throw new RuntimeException(message);
                 }
            }

            return SingleRechargeResponseDto.builder()
                    .id(request.getId())
                    .authorizationUrl(request.getAuthorizationUrl())
                    .message(request.getMessage())
                    .status(request.getStatus())
                    .paymentMode(paymentRequest.getPaymentMode())
                    .redirectUrl(paymentRequest.getRedirectUrl())
                    .build();
        }

        final String message = "Error in Start Recharge CheckParameter Failed or Exception Thrown in Execution";
        log.error(message);
        throw new RuntimeException(message);
    }

    public RechargeStatus finishRecharge(String id) {
        RechargeStatus status = null;

        log.info(String.format("Fulfilling Recharge Request %s", id));

        if (id == null) {
            throw new RuntimeException("Recharge ID NULL Set it Cannot be NULL");
        }

        SingleRechargeRequest request = singleRechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            throw new RuntimeException(String.format("Recharge Request (%s) is either NOT AVAILABLE or CLOSED", id));
        }

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            throw new RuntimeException("Payment has not been made or notification delayed, please try again");
        }

        List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());
            status = recharge.recharge(request);

            if (status.getStatus() == HttpStatus.OK) {
                singleRechargeMapper.closeRequest(id);

                // If it is a scheduled Recharge, it will have been paid for and transaction logged at the time it was Scheduled
                if (request.getScheduledRequestId() == null) {
                    saveTransaction(request);
                }
            }
        }

        return status;
    }

    public List<DataPlanDto> getDataPlans(String code) {

        ServiceAction action = serviceActionMapper.findByCode(code);

        if (action == null || !Objects.equals(action.getActionId(), DATA_ACTION)) {
            throw new RuntimeException(String.format("Unknown data plan (%s) Or Data Plan is not for DATA", code));
        }

        AbstractFactory factory = getFactory(action.getId());

        if (factory == null) {
            throw new RuntimeException(String.format("Factory not found for Product with code (%s)", code));
        }

        DataEnquiry enquiry = factory.getPlans(code);
        return enquiry.getDataPlans(code);
    }

    private Boolean checkPayment(String id) {
        PaymentRequestDto dto
                = restTemplate.getForObject(BASE_LOCAL_STATIC + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }

    private AbstractFactory getFactory(Integer factoryType) {
        List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(factoryType);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            return producer.getFactory(rechargeProviderCode);
        }

        return null;
    }

    private void saveTransaction(SingleRechargeRequest request) {

        log.info("UserId : {}", request.getUserId());

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(request.getServiceId())
                .requestId(request.getId())
                .serviceCost(request.getServiceCost())
                .transactionDate(new Date().toString())
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .build();
        try {
            jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
        } catch (JsonProcessingException e) {
            log.error("Error sending JMS Transaction Message to Wallet service {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static  PaymentRequestDto initializePayment(SingleRechargeRequest request) {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .paymentMode(request.getPaymentMode())
                .build();

        String uri = null;
        RestTemplate restTemplate = new RestTemplate();

        if (K.getUserId() == null) { // Anonymous Login
            uri = "api/v1/pay";
        } else {
            uri = "api/v1/payment";
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        }

        return restTemplate.postForObject(BASE_LOCAL_STATIC + uri, dto, PaymentRequestDto.class);
    }

    public static Boolean checkParameters (SingleRechargeRequest request, SingleRechargeRequestDto dto) {
        String serviceAction = null;
        String rechargeProviderCode = null;

        SingleRechargeMapper mapper = ApplicationContextProvider.getBean(SingleRechargeMapper.class);
        List<RechargeFactoryParameters> parameters = mapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", dto.getServiceCode()));
        }

        FactoryProducer factoryProducer = ApplicationContextProvider.getBean(FactoryProducer.class);
        AbstractFactory factory = factoryProducer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", dto.getServiceCode()));
        }

        if (dto.getServiceCost() == null) { // Service with Fixed Cost
            DataEnquiry enquiry = factory.getPlans(serviceAction);
            DataPlanDto planDto = enquiry.getPlan(request.getProductId());
            BigDecimal cost = new BigDecimal(planDto.getPrice());
            request.setServiceCost(cost);
        }

        ParameterCheck parameterCheck = factory.getCheck(serviceAction);

        if (!parameterCheck.check(request)) {
            throw new RuntimeException(String.format("Missing Parameter in Request (%s)", request.getServiceCode()));
        }

        return true;
    }
}
