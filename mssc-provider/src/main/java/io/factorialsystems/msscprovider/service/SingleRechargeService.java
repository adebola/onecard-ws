package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.recharge.ringo.response.ProductItem;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SingleRechargeService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final FactoryProducer producer;
    private final ParameterCache parameterCache;
    private final RestTemplate restTemplate;
    private final SingleRechargeMapper singleRechargeMapper;
    private final ServiceActionMapper serviceActionMapper;
    private final RechargeMapstructMapper rechargeMapstructMapper;

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
            log.info(String.format("Saving Recharge Request %s for %s", request.getId(), K.getUserName()));

            // Bulk and Scheduled Requests are always handled asynchronously
            if (request.getBulkRequestId() != null || request.getScheduledRequestId() != null) {
                request.setAsyncRequest(true);
            }

            singleRechargeMapper.save(request);

            if (request.getPaymentMode().equals("wallet") && request.getAsyncRequest()) {
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

            // Some Requests cannot complete successfully asynchronously such as Electricity Recharge and Spectranet
            // that dispense PINs/Codes to be consumed at some other independent touch points
            // The rationale is as follows
            // If a Request is flagged as an Asynchronous request such as Airtime Recharge it will be allowed to
            // If it is a Request with a Paystack payment it must return to the user with the payment details before
            // the actual recharge is performed by calling finishRecharge after payment
            // If it is a Scheduled or Bulk Request it also will run Asynchronously

            if (request.getAsyncRequest() || request.getPaymentMode().equals("paystack")) {
                return SingleRechargeResponseDto.builder()
                        .id(request.getId())
                        .authorizationUrl(request.getAuthorizationUrl())
                        .amount(request.getServiceCost())
                        .message(request.getMessage())
                        .status(request.getStatus())
                        .paymentMode(paymentRequest.getPaymentMode())
                        .redirectUrl(paymentRequest.getRedirectUrl())
                        .build();
            } else {
                RechargeStatus status = finishLocalRecharge(request);

                if (status.getStatus() == HttpStatus.OK) {
                    return SingleRechargeResponseDto.builder()
                            .message(status.getMessage())
                            .build();
                } else {
                    log.error(String.format("Error in Synchronous Recharge Request (%s), reason : %s", request.getServiceCode(), status.getMessage()));
                }
            }
        }

        final String message = String.format("Error in Start Recharge (%s), CheckParameter may have Failed or Exception Thrown in Execution", dto.getServiceCode());
        log.error(message);
        throw new RuntimeException(message);
    }

    private RechargeStatus finishLocalRecharge(SingleRechargeRequest request) {

        RechargeStatus status = null;

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            throw new RuntimeException("Payment has not been made or notification delayed, please try again");
        }

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());
            status = recharge.recharge(request);

            if (status.getStatus() == HttpStatus.OK) {
                singleRechargeMapper.closeRequest(request.getId());

                // If it is a scheduled Recharge, it will have been paid for and transaction logged at the time it was Scheduled
                if (request.getScheduledRequestId() == null) {
                    saveTransaction(request);
                }
            }
        }

        return status;

    }

    public RechargeStatus finishRecharge(String id) {

        log.info(String.format("Fulfilling Recharge Request %s", id));

        if (id == null) {
            throw new RuntimeException("Recharge ID NULL Set it Cannot be NULL");
        }

        SingleRechargeRequest request = singleRechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            throw new RuntimeException(String.format("Recharge Request (%s) is either NOT AVAILABLE or CLOSED", id));
        }

        return finishLocalRecharge(request);
    }

    public ExtraDataPlanDto getExtraDataPlans(ExtraPlanRequestDto dto) {
        ServiceAction action = serviceActionMapper.findByCode(dto.getServiceCode());

        if (action == null) {
            throw new RuntimeException(String.format("Unknown Extra Data plan (%s)", dto.getServiceCode()));
        }

        AbstractFactory factory = getFactory(action.getId());

        if (factory == null) {
            throw new RuntimeException(String.format("Factory not found for Product with code (%s)", dto.getServiceCode()));
        }

        ExtraDataEnquiry enquiry = factory.getExtraPlans(dto.getServiceCode());
        return enquiry.getExtraPlans(dto);
    }

    @Cacheable("dataplans")
    public List<DataPlanDto> getDataPlans(String code) {

        ServiceAction action = serviceActionMapper.findByCode(code);

//        || !Objects.equals(action.getActionId(), DATA_ACTION)
        if (action == null) {
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
        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(factoryType);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            return producer.getFactory(rechargeProviderCode);
        }

        return null;
    }

    public PagedDto<SingleRechargeRequestDto> getUserRecharges(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.findRequestsByUserId(K.getUserId());

        return createDto(requests);
    }

    public PagedDto<SingleRechargeRequestDto> search(String search, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.search(search);

        return createDto(requests);
    }

    public SingleRechargeRequestDto getRecharge(String id) {
        SingleRechargeRequest request = singleRechargeMapper.findById(id);
        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    private void saveTransaction(SingleRechargeRequest request) {

        log.info("Saving Transaction for User : {}", request.getUserId());

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

    public static PaymentRequestDto initializePayment(SingleRechargeRequest request) {
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

    public static Boolean checkParameters(SingleRechargeRequest request, SingleRechargeRequestDto dto) {
        String serviceAction = null;
        String rechargeProviderCode = null;

        ParameterCache cache = ApplicationContextProvider.getBean(ParameterCache.class);
        List<RechargeFactoryParameters> parameters = cache.getFactoryParameter(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
            request.setAsyncRequest(parameter.getAsync());
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", dto.getServiceCode()));
        }

        FactoryProducer factoryProducer = ApplicationContextProvider.getBean(FactoryProducer.class);
        AbstractFactory factory = factoryProducer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", dto.getServiceCode()));
        }

        if (dto.getProductId() != null) {
            DataEnquiry enquiry  = factory.getPlans(serviceAction);

            if (enquiry == null) {
                ExtraDataEnquiry extraDataEnquiry = factory.getExtraPlans(serviceAction);

                if (extraDataEnquiry != null) {
                    ExtraDataPlanDto extraDataPlanDto = extraDataEnquiry.getExtraPlans (
                            ExtraPlanRequestDto.builder()
                                    .recipient(dto.getRecipient())
                                    .serviceCode(dto.getServiceCode())
                                    .build()
                    );

                   Integer price = extraDataPlanDto.getObject().stream()
                            .filter(r -> r.getCode().equals(dto.getProductId()))
                            .findFirst()
                            .map(ProductItem::getPrice)
                            .orElseThrow(() -> new ResourceNotFoundException("ExtraDataPlanDto", "ProductCode", dto.getProductId()));

                    if  (price > 0) {
                        request.setServiceCost(new BigDecimal(price));
                    }
                }

            } else {
                DataPlanDto planDto = enquiry.getPlan(dto.getProductId());
                request.setServiceCost(new BigDecimal(planDto.getPrice()));
            }
        }

        if (request.getServiceCost() == null) {
            throw new RuntimeException(String.format("Unable to determine Price of Recharge Request (%s) by (%s)", request.getId(),request.getUserId()));
        }

        ParameterCheck parameterCheck = factory.getCheck(serviceAction);

        if (!parameterCheck.check(request)) {
            throw new RuntimeException(String.format("Missing / Wrong Parameter in Request (%s)", request.getServiceCode()));
        }

        return true;
    }

    private PagedDto<SingleRechargeRequestDto> createDto(Page<SingleRechargeRequest> requests) {
        PagedDto<SingleRechargeRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(rechargeMapstructMapper.listRechargeToRechargeDto(requests.getResult()));
        return pagedDto;
    }
}
