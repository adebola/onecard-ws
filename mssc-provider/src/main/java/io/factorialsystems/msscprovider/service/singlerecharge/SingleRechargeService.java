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
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.*;
import io.factorialsystems.msscprovider.dto.search.SearchSingleFailedRechargeDto;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.helper.PaymentHelper;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.properties.GeneralProperties;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.recharge.ringo.response.ProductItem;
import io.factorialsystems.msscprovider.service.MailService;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleDownloadRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRefundRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleResolveRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRetryRecharge;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
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
    private final GeneralProperties generalProperties;
    private final ServiceActionMapper serviceActionMapper;
    private final SingleRetryRecharge singleRetryRecharge;
    private final SingleRechargeMapper singleRechargeMapper;
    private final SingleRefundRecharge singleRefundRecharge;
    private final SingleResolveRecharge singleResolveRecharge;
    private final SingleDownloadRecharge singleDownloadRecharge;
    private final RechargeMapstructMapper rechargeMapstructMapper;

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
            log.info(String.format("Saving Recharge Request %s for %s", request.getId(), ProviderSecurity.getUserName()));

            if (request.getStatus() != 200) {
                final String msg = String.format("PaymentFailed-%s/%s", paymentRequest.getMessage(), request.getId());
                singleRechargeMapper.save(request);

                Map<String, String> resultsMap = new HashMap<>();

                resultsMap.put("id", request.getId());
                resultsMap.put("results", "FAILED PAYMENT");
                resultsMap.put("failedMessage", msg);

                log.error(msg);

                singleRechargeMapper.closeAndFailRequest(resultsMap);

                return SingleRechargeResponseDto.builder()
                        .status(400)
                        .message(msg)
                        .build();
            }

            // Bulk and Scheduled Requests are always handled asynchronously
            if (request.getBulkRequestId() != null || request.getScheduledRequestId() != null) {
                request.setAsyncRequest(true);
            }

            singleRechargeMapper.save(request);

            if (request.getPaymentMode().equals("wallet") && request.getAsyncRequest()) {
                try {
                    AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                            .id(request.getId())
                            .email(ProviderSecurity.getEmail())
                            .name(ProviderSecurity.getUserName())
                            .balance(paymentRequest.getBalance())
                            .build();
                    jmsTemplate.convertAndSend(JMSConfig.SINGLE_RECHARGE_QUEUE, objectMapper.writeValueAsString(asyncRechargeDto));
                } catch (JsonProcessingException e) {
                    log.error("Error sending Single Recharge Service to Self {}", e.getMessage());

                    return SingleRechargeResponseDto.builder()
                            .status(400)
                            .message("JMS Error sending message, please contact Onecard Support")
                            .build();
                }
            }

            // Some Requests cannot complete successfully asynchronously such as Electricity Recharge and Spectranet
            // that dispense PINs/Codes to be consumed at some other independent touch points
            // The rationale is as follows
            // If a Request is flagged as an Asynchronous request such as Airtime Recharge it will be allowed to
            // If it is a Request with a Paystack payment it must return to the user with the payment details before
            // the actual recharge is performed by calling finishRecharge after payment
            // If it is a Scheduled or Bulk Request it also will run Asynchronously automatically

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
            } else { // Synchronous Wallet Payment
                AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                        .id(dto.getId())
                        .email(ProviderSecurity.getEmail())
                        .name(ProviderSecurity.getUserName())
                        .balance(paymentRequest.getBalance())
                        .build();

                RechargeStatus status = finishLocalRecharge(request, asyncRechargeDto);

                if (status.getStatus() == HttpStatus.OK) {
                    return SingleRechargeResponseDto.builder()
                            .status(200)
                            .message(status.getMessage())
                            .build();
                } else {
                    final String errorMessage = String.format("Error in Synchronous Recharge Request (%s), reason : %s", request.getServiceCode(), status.getMessage());
                    log.error(errorMessage);
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

            // If it is a scheduled Recharge, it will have been paid for and transaction logged at the time it was Scheduled
            if (request.getScheduledRequestId() == null) {
                saveTransaction(request);
            }

            Map<String, String> resultsMap = new HashMap<>();
            resultsMap.put("id", request.getId());
            resultsMap.put("provider", String.valueOf(parameter.getRechargeProviderId()));
            resultsMap.put("results", status.getResults());
            request.setResults(status.getResults());
            sendMail(request, dto, status);

            if (status.getStatus() != HttpStatus.OK) {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("id", request.getId());
                paramMap.put("message", status.getMessage());

                singleRechargeMapper.failRequest(paramMap);
                singleRefundRecharge.asyncRefundRecharge(request);
            }

            singleRechargeMapper.closeRequest(resultsMap);
        }

        return status;
    }

    public MessageDto refundRecharge(String id) {
        return singleRefundRecharge.refundRecharge(id);
    }

    public ResolveRechargeDto resolveRecharge(String id, ResolveRechargeDto dto) {
        dto.setRechargeId(id);
        dto.setResolvedBy(ProviderSecurity.getUserName());

        return singleResolveRecharge.resolve(dto)
                .orElseThrow(() -> new RuntimeException(String.format("Error Resolving Recharge %s", id)));
    }

    public RechargeStatus retryRecharge(String id, String recipient) {
        return singleRetryRecharge.retryRecharge(id, recipient);
    }

    public static void sendMail(SingleRechargeRequest request, AsyncRechargeDto dto, RechargeStatus status) {

        if (dto.getEmail() == null || dto.getName() == null) {
            log.info("Unable to Send E-mail for Transaction, No E-mail Address found");
            return;
        }

        String result = null;

        if (status.getStatus() == HttpStatus.OK) {
            result = " is successful";
        } else {
            result = "Failed";
        }

        String message = null;

        if (dto.getBalance() == null) {
            message = String.format("Dear %s\n\nYour recharge of %s to %s for %.2f %s",
                    dto.getName(), request.getServiceCode(), request.getRecipient(), request.getServiceCost(), result);
        } else {
            message = String.format("Dear %s\n\nYour recharge of %s to %s for %.2f %s current wallet balance %.2f",
                    dto.getName(), request.getServiceCode(), request.getRecipient(), request.getServiceCost(), result, dto.getBalance());
        }

        if (request.getResults() != null) {
            message = String.format("%s \n\nRecharge Results are %s", message, request.getResults());
        }

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(message)
                .to(dto.getEmail())
                .subject("Single Recharge Report")
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

    public List<DataPlanDto> getDataPlans(String code) {

        ServiceAction action = serviceActionMapper.findByCode(code);

        if (action == null) {
            throw new RuntimeException(String.format("Unknown data plan (%s) Or Data Plan is not for DATA", code));
        }

        AbstractFactory factory = Optional.ofNullable(getFactory(action.getId()))
                .orElseThrow(() -> new RuntimeException(String.format("Factory not found for Product with code (%s)", code)));

        DataEnquiry enquiry = factory.getPlans(code);

        if (enquiry == null) {
            final String message = String.format("Unable to get data plans for service: %s", code);
            log.error(message);

            throw new ResourceNotFoundException("DataEnquiry", "code", code);
        }

        return enquiry.getDataPlans(code);
    }

    private Boolean checkPayment(String id) {
        PaymentRequestDto dto
                = restTemplate.getForObject(generalProperties.getBaseUrl() + "api/v1/pay/" + id, PaymentRequestDto.class);

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

    public PagedDto<SingleRechargeRequestDto> search(SearchSingleRecharge search, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.search(search);

        return createDto(requests);
    }


    public PagedDto<SingleRechargeRequestDto> adminFailedSearch(SearchSingleFailedRechargeDto dto, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.adminFailedSearch(dto);

        return createDto(requests);
    }

    public SingleRechargeRequestDto getRecharge(String id) {
        SingleRechargeRequest request = singleRechargeMapper.findById(id);
        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    public PagedDto<SingleRechargeRequestDto> getFailedTransactions(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.findFailedRequests();

        log.info("Retrieving All Single Failed Transactions");

        return createDto(requests);
    }

    public PagedDto<SingleRechargeRequestDto> getFailedUnresolvedTransactions(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<SingleRechargeRequest> requests = singleRechargeMapper.findFailedUnResolvedRequests();

        log.info("Retrieving Failed Unresolved Transactions");
        return createDto(requests);
    }

    public InputStreamResource getRechargesByUserId(String id) {
        return singleDownloadRecharge.downloadFailedByUserId(id);
    }

    public InputStreamResource getRechargeByDateRange(DateRangeDto dto) {
        dto.setId(ProviderSecurity.getUserId());
        return singleDownloadRecharge.downloadRechargeByDateRange(dto);
    }

    public InputStreamResource getFailedRecharges(String type) {
        return singleDownloadRecharge.downloadFailed(type);
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

    private PaymentRequestDto initializePayment(SingleRechargeRequest request) {
        PaymentHelper helper = PaymentHelper.builder()
                .cost(request.getServiceCost())
                .paymentMode(request.getPaymentMode())
                .redirectUrl(request.getRedirectUrl())
                .build();

        return helper.initializePayment();
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
            DataEnquiry enquiry = factory.getPlans(serviceAction);
            log.info("Product Id {}, determine price", dto.getProductId());

            if (enquiry == null) {
                log.info("Null DataEnquiry, querying for Extra Data Enquiry for Action {}, ServiceCode {}", serviceAction, dto.getServiceCode());

                ExtraDataEnquiry extraDataEnquiry = factory.getExtraPlans(dto.getServiceCode());

                if (extraDataEnquiry != null) {
                    ExtraDataPlanDto extraDataPlanDto = extraDataEnquiry.getExtraPlans(
                            ExtraPlanRequestDto.builder()
                                    .recipient(dto.getRecipient())
                                    .serviceCode(dto.getServiceCode())
                                    .build()
                    );

                    log.info("Querying for ExtraDataEnquiry Results: {}", extraDataPlanDto);

                    Integer price = extraDataPlanDto.getObject().stream()
                            .filter(r -> r.getCode().equals(dto.getProductId()))
                            .findFirst()
                            .map(ProductItem::getPrice)
                            .orElseThrow(() -> new ResourceNotFoundException("ExtraDataPlanDto", "ProductId", dto.getProductId()));

                    if (price > 0) {
                        request.setServiceCost(new BigDecimal(price));
                    }
                }
            } else {
                DataPlanDto planDto = enquiry.getPlan(dto.getProductId(), dto.getServiceCode());
                request.setServiceCost(new BigDecimal(planDto.getPrice()));
            }
        }

        if (request.getServiceCost() == null) {
            throw new RuntimeException(
                    String.format("Unable to determine Price of Recharge Request (%s/%s) by (%s) User may not be eligible to subscribe to the service",
                            request.getServiceCode(), request.getProductId(), request.getUserId()));
        }

        ParameterCheck parameterCheck = factory.getCheck(serviceAction);

        if (!parameterCheck.check(request)) {
            log.error("Recharge Parameter Check Failure {}", request);
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
