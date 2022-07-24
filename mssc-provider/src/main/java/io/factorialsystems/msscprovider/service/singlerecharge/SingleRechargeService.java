package io.factorialsystems.msscprovider.service.singlerecharge;

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
import io.factorialsystems.msscprovider.service.MailService;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRefundRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleResolveRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRetryRecharge;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SingleRechargeService {
    private final JmsTemplate jmsTemplate;
    private final FactoryProducer producer;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final ParameterCache parameterCache;
    private final ServiceActionMapper serviceActionMapper;
    private final SingleRetryRecharge singleRetryRecharge;
    private final SingleRechargeMapper singleRechargeMapper;
    private final SingleRefundRecharge singleRefundRecharge;
    private final SingleResolveRecharge singleResolveRecharge;
    private final RechargeMapstructMapper rechargeMapstructMapper;

    private static String BASE_LOCAL_STATIC;

    @Value("${api.local.host.baseurl}")
    public void setNameStatic(String baseLocal) {
        SingleRechargeService.BASE_LOCAL_STATIC = baseLocal;
    }

    public SingleRechargeResponseDto startRecharge(SingleRechargeRequestDto dto) {
        SingleRechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);

        if (checkParameters(request, dto)) {
            PaymentRequestDto paymentRequest = Optional.ofNullable(initializePayment(request))
                    .orElseThrow(() -> new RuntimeException("Error Initializing Payment Please contact OneCard Support"));

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
                        AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                                .id(request.getId())
                                .email(K.getEmail())
                                .build();
                        jmsTemplate.convertAndSend(JMSConfig.SINGLE_RECHARGE_QUEUE, objectMapper.writeValueAsString(asyncRechargeDto));
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
                AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                        .id(dto.getId())
                        .email(K.getEmail())
                        .build();

                RechargeStatus status = finishLocalRecharge(request, asyncRechargeDto);

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

    public RechargeStatus finishRecharge(AsyncRechargeDto dto) {
        final String id = dto.getId();

        log.info(String.format("Fulfilling Recharge Request %s", id));
        SingleRechargeRequest request = singleRechargeMapper.findById(id);

        if (request == null || request.getClosed()) {
            final String errorMessage = String.format("Recharge Request (%s) is %s", id, request == null ? "NOT AVAILABLE" : "CLOSED");
            log.error(errorMessage);

            return RechargeStatus.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(errorMessage)
                    .build();
        }

        return finishLocalRecharge(request, dto);
    }

    private RechargeStatus finishLocalRecharge(SingleRechargeRequest request, AsyncRechargeDto dto) {

        RechargeStatus status = null;

        if (request.getPaymentId() != null && !checkPayment(request.getPaymentId())) {
            final String errorMessage = String.format("Error Fulfilling Single Recharge Request %s, Payment has not been made", request.getId());
            log.error(errorMessage);

            return RechargeStatus.builder()
                    .message(errorMessage)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());
            status = recharge.recharge(request);

            singleRechargeMapper.closeRequest(request.getId());

            // If it is a scheduled Recharge, it will have been paid for and transaction logged at the time it was Scheduled
            if (request.getScheduledRequestId() == null) {
                saveTransaction(request);
            }

            if (status.getStatus() == HttpStatus.OK) {
                if (request.getUserId() != null) sendMail(request, dto, status);
            } else {
                singleRefundRecharge.refundRechargeRequest(request);
            }
        }

        return status;
    }

    public MessageDto refundRecharge(String id) {
        return singleRefundRecharge.refundRecharge(id);
    }

    public ResolveRechargeDto resolveRecharge(String id, ResolveRechargeDto dto) {
        dto.setRechargeId(id);
        dto.setResolvedBy(K.getUserName());

        return singleResolveRecharge.resolve(dto)
                .orElseThrow(() -> new RuntimeException(String.format("Error Resolving Recharge %s", id)));
    }

    public RechargeStatus retryRecharge(String id, String recipient) {
        return singleRetryRecharge.retryRecharge(id, recipient);
    }

    public static void sendMail(SingleRechargeRequest request, AsyncRechargeDto dto, RechargeStatus status) {

        if (dto.getEmail() == null) {
            log.info("Unable to Send E-mail for Transaction, No E-mail Address found");
        }

        String result = null;

        if (status.getStatus() == HttpStatus.OK) {
            result = "Succeeded";
        } else {
            result = String.format("Failed:, Reason %s", status.getMessage());
        }

        final String message =
                String.format("The Recharge of %s to %s for %.2f %s", request.getServiceCode(), request.getRecipient(), request.getServiceCost(), result);

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(message)
                .to(dto.getEmail())
                .subject("Recharge Report")
                .build();

        MailService mail = ApplicationContextProvider.getBean(MailService.class);
        SingleRechargeMapper mapper = ApplicationContextProvider.getBean(SingleRechargeMapper.class);

        final String emailId = mail.sendMailWithOutAttachment(mailMessageDto);

        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("id", dto.getId());
        emailMap.put("emailId", emailId);
        mapper.setEmailId(emailMap);
    }

    public ExtraDataPlanDto getExtraDataPlans(ExtraPlanRequestDto dto) {
        ServiceAction action = serviceActionMapper.findByCode(dto.getServiceCode());

        if (action == null) {
            throw new RuntimeException(String.format("Unknown Extra Data plan (%s)", dto.getServiceCode()));
        }

        AbstractFactory factory = Optional.ofNullable(getFactory(action.getId()))
                .orElseThrow(() -> new RuntimeException(String.format("Factory not found for Product with code (%s)", dto.getServiceCode())));

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

        AbstractFactory factory = Optional.ofNullable(getFactory(action.getId()))
                .orElseThrow(() -> new RuntimeException(String.format("Factory not found for Product with code (%s)", code)));

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

    public PagedDto<SingleRechargeRequestDto> getUserRecharges(String userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.findRequestsByUserId(userId);

        return createDto(requests);
    }

    public PagedDto<SingleRechargeRequestDto> search(String search, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.search(search);

        return createDto(requests);
    }

    public PagedDto<SingleRechargeRequestDto> adminSearch(SearchSingleRechargeDto dto) {
        PageHelper.startPage(dto.getPageNumber(), dto.getPageSize());
        Page<SingleRechargeRequest> requests = singleRechargeMapper.adminSearch(dto);

        return createDto(requests);
    }

    public SingleRechargeRequestDto getRecharge(String id) {
        SingleRechargeRequest request = singleRechargeMapper.findById(id);
        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    @SneakyThrows
    public static void saveTransaction(SingleRechargeRequest request) {

        log.info("Saving Transaction for User : {}", request.getUserId());

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(request.getServiceId())
                .requestId(request.getId())
                .serviceCost(request.getServiceCost())
                .transactionDate(new Date().toString())
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .build();

        JmsTemplate template = ApplicationContextProvider.getBean(JmsTemplate.class);
        ObjectMapper mapper = ApplicationContextProvider.getBean(ObjectMapper.class);

        template.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, mapper.writeValueAsString(requestTransactionDto));
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
