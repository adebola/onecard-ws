package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.RechargeMapper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.PaymentRequestDto;
import io.factorialsystems.msscprovider.dto.RechargeRequestDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeService {
    private final FactoryProducer producer;
    private final RestTemplate restTemplate;
    private final RechargeMapper rechargeMapper;
    private final ServiceActionMapper serviceActionMapper;
    private final RechargeMapstructMapper rechargeMapstructMapper;

    public static final Integer AIRTIME_ACTION = 1;
    public static final Integer DATA_ACTION = 2;
    public static final Integer ELECTRICITY_ACTION = 3;

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    public RechargeRequestDto startRecharge(RechargeRequestDto dto) {
        RechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);

        AbstractFactory factory = getFactory(request.getServiceId());

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", dto.getServiceCode()));
        }

        String serviceAction = null;

        List<RechargeFactoryParameters> parameters = rechargeMapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for", dto.getServiceCode()));
        }

        ParameterCheck parameterCheck = factory.getCheck(serviceAction);

        if (!parameterCheck.check(request)) {
            throw new RuntimeException(String.format("Missing Parameter in Request (%s)", request.getServiceCode()));
        }

        if (dto.getServiceCost() == null) { // Service with Fixed Cost
            DataEnquiry enquiry = factory.getPlans(serviceAction);
            DataPlanDto planDto = enquiry.getPlan(request.getProductId());
            BigDecimal cost = new BigDecimal(planDto.getPrice());
            request.setServiceCost(cost);
        }

        PaymentRequestDto paymentRequest = initializePayment(request);
        request.setPaymentId(paymentRequest.getId());
        request.setAuthorizationUrl(paymentRequest.getAuthorizationUrl());

        if (paymentRequest.getRedirectUrl() != null) {
            request.setRedirectUrl(paymentRequest.getRedirectUrl());
        }

        rechargeMapper.save(request);

        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    // Change Id of RechargeRequest to UUID
    public Boolean finishRecharge(Integer id) {

        if (id == null) {
            throw new RuntimeException(String.format("Unable to find Recharge Request %d", id));
        }

        RechargeRequest request = rechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            throw new RuntimeException(String.format("Recharge Request (%s) is either NOT AVAILABLE or CLOSED",id));
        }

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            throw new RuntimeException("Payment has not been made or notification delayed, please try again");
        }

        List<RechargeFactoryParameters> parameters = rechargeMapper.factory(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());
            RechargeStatus status = recharge.recharge(request);

            if (status.getStatus() == HttpStatus.OK) {
                rechargeMapper.closeRequest(id);
                return true;
            }
        }

        return false;
    }

    public List<DataPlanDto> getDataPlans(String code) {

        ServiceAction action = serviceActionMapper.findByCode(code);

        if (action == null  || !Objects.equals(action.getActionId(), DATA_ACTION)) {
            throw new RuntimeException(String.format("Unknown data plan (%s) Or Data Plan is not for DATA", code));
        }

        AbstractFactory factory = getFactory(action.getId());

        if (factory == null) {
            throw new RuntimeException(String.format("Factory not found for Product with code (%s)", code));
        }

        DataEnquiry enquiry = factory.getPlans(code);
        return enquiry.getDataPlans(code);
    }

    private PaymentRequestDto initializePayment(RechargeRequest request) {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .build();

        return restTemplate.postForObject(baseLocalUrl + "api/v1/pay", dto, PaymentRequestDto.class);
    }

    private Boolean checkPayment(String id) {
        PaymentRequestDto dto
                = restTemplate.getForObject(baseLocalUrl + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }

    private AbstractFactory getFactory(Integer factoryType) {
        List<RechargeFactoryParameters> parameters = rechargeMapper.factory(factoryType);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            return producer.getFactory(rechargeProviderCode);
        }

        return null;
    }
}
