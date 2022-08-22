package io.factorialsystems.msscprovider.service.bulkrecharge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.RequestTransactionDto;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeResponseDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkFailedRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchIndividualDto;
import io.factorialsystems.msscprovider.dto.status.StatusMessageDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.mapper.recharge.NewBulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.service.MailService;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkRefundRecharge;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkResolveRecharge;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkRetryRecharge;
import io.factorialsystems.msscprovider.service.file.BulkRequestExcelWriter;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
import io.factorialsystems.msscprovider.service.model.ServiceHelper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewBulkRechargeService {
    private final ServiceHelper helper;
    private final JmsTemplate jmsTemplate;
    private final MailService mailService;
    private final FactoryProducer producer;
    private final ObjectMapper objectMapper;
    private final FileUploader fileUploader;
    private final BulkRetryRecharge bulkRetryRecharge;
    private final BulkRefundRecharge bulkRefundRecharge;
    private final BulkResolveRecharge bulkResolveRecharge;
    private final ParameterCache parameterCache;
    private final BulkRequestExcelWriter excelWriter;
    private final NewBulkRechargeMapstructMapper mapper;
    private final NewBulkRechargeMapper newBulkRechargeMapper;

    public void uploadRecharge(MultipartFile file) {
        UploadFile uploadFile = fileUploader.uploadFile(file);
        ExcelReader excelReader = new ExcelReader(uploadFile);

        NewBulkRechargeRequestDto newRequestDto = new NewBulkRechargeRequestDto();
        newRequestDto.setPaymentMode(K.WALLET_PAY_MODE);
        List<IndividualRequestDto> individualRequests = excelReader.readContents();

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new RuntimeException("Empty or Un-Populated Excel file");
        }

        newRequestDto.setRecipients(individualRequests);
        saveService(newRequestDto);
    }

    @SneakyThrows
    @Transactional
    public NewBulkRechargeResponseDto saveService(NewBulkRechargeRequestDto dto) {

        if (dto.getRecipients() == null || dto.getRecipients().isEmpty()) {
            final String errorMessage = "No Recipients specified, nothing todo";
            log.error(errorMessage);

            return NewBulkRechargeResponseDto.builder()
                    .status(300)
                    .message(errorMessage)
                    .build();
        }

        NewBulkRechargeRequest request = mapper.rechargeDtoToRecharge(dto);
        PaymentRequestDto requestDto = helper.initializePayment(request);

        if (requestDto.getPaymentMode().equals(K.WALLET_PAY_MODE) && requestDto.getStatus() != 200) {
            return NewBulkRechargeResponseDto.builder()
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
            i.setExternalRequestId(UUID.randomUUID().toString());
            i.setBulkRequestId(requestId);
            i.setAutoRequestId(dto.getAutoRequestId());
        });
        newBulkRechargeMapper.saveBulkIndividualRequests(request.getRecipients());

        if (request.getPaymentMode().equals(K.WALLET_PAY_MODE)) {
            saveTransaction(request);

            AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                    .id(requestId)
                    .email(K.getEmail())
                    .name(K.getUserName())
                    .balance(requestDto.getBalance())
                    .build();

            log.info(String.format("Sending message for Asynchronous processing of Bulk Recharge Request (%s)", requestId));
            jmsTemplate.convertAndSend(JMSConfig.NEW_BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(asyncRechargeDto));
        }

        return NewBulkRechargeResponseDto.builder()
                .id(requestId)
                .message("Bulk Recharge Request Submitted Successfully")
                .status(200)
                .totalCost(request.getTotalServiceCost())
                .paymentMode(request.getPaymentMode())
                .redirectUrl(request.getRedirectUrl())
                .authorizationUrl(request.getAuthorizationUrl())
                .build();
    }

    public void runBulkRecharge(AsyncRechargeDto dto) {
        final String id = dto.getId();

        log.info(String.format("received message for processing of New Bulk Recharge Request (%s)", id));

        NewBulkRechargeRequest request = newBulkRechargeMapper.findBulkRechargeById(id);

        if (request == null || request.getClosed()) {
            final String errMessage = String.format("BulkRechargeRequest request (%s) is either closed or non-existent", id);
            log.error(errMessage);
            return;
        }

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

        newBulkRechargeMapper.setRunning(id);

        try {
            individualRequests.forEach(individualRequest -> {
                List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(individualRequest.getServiceId());

                if (parameters != null && !parameters.isEmpty()) {
                    RechargeFactoryParameters parameter = parameters.get(0);
                    String rechargeProviderCode = parameter.getRechargeProviderCode();
                    AbstractFactory factory = producer.getFactory(rechargeProviderCode);
                    String serviceAction = parameter.getServiceAction();

                    SingleRechargeRequest singleRechargeRequest = SingleRechargeRequest.builder()
                            .serviceId(individualRequest.getServiceId())
                            .serviceCode(individualRequest.getServiceCode())
                            .serviceCost(individualRequest.getServiceCost())
                            .id(individualRequest.getExternalRequestId())
                            .recipient(individualRequest.getRecipient())
                            .productId(individualRequest.getProductId())
                            .bulkRequestId(individualRequest.getBulkRequestId())
                            .build();

                    ParameterCheck parameterCheck = factory.getCheck(serviceAction);

                    if (parameterCheck.check(singleRechargeRequest)) {
                        Recharge recharge = factory.getRecharge(parameter.getServiceAction());

                        // Work Around for Ringo, which has scaling issues
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            log.error(String.format("Thread Sleep error : %s", e.getMessage()));
                        }

                        RechargeStatus rechargeStatus = recharge.recharge(singleRechargeRequest);

                        if (rechargeStatus.getStatus() != HttpStatus.OK) {
                            IndividualRequestFailureNotification notification = IndividualRequestFailureNotification
                                    .builder()
                                    .errorMsg(rechargeStatus.getMessage().length() > 255 ? rechargeStatus.getMessage().substring(0, 255) : rechargeStatus.getMessage())
                                    .id(individualRequest.getId())
                                    .build();

                            newBulkRechargeMapper.failIndividualRequest(notification);
                        }
                    } else {
                        final String errorMessage =
                                String.format("Invalid Parameters in Request Action: (%s) serviceCode: (%s), Recipient: (%s) Made By: (%s)",
                                        serviceAction, individualRequest.getServiceCode(), individualRequest.getRecipient(), K.getEmail());
                        log.error(errorMessage);

                        IndividualRequestFailureNotification notification = IndividualRequestFailureNotification
                                .builder()
                                .errorMsg(errorMessage.substring(0, 255))
                                .id(individualRequest.getId())
                                .build();

                        newBulkRechargeMapper.failIndividualRequest(notification);
                    }
                } else {
                    IndividualRequestFailureNotification notification = IndividualRequestFailureNotification
                            .builder()
                            .errorMsg(String.format("Unable to determine service for Id %d", individualRequest.getServiceId()))
                            .id(individualRequest.getId())
                            .build();

                    newBulkRechargeMapper.failIndividualRequest(notification);
                }
            });

            if (request.getPaymentMode().equals(K.PAYSTACK_PAY_MODE) && request.getScheduledRequestId() == null) {
                saveTransaction(request);
            }

            retryFailedRecharges(id);
            sendReport(dto, request);
            refundFailedRecharges(id);
        } catch (Exception ex) {
            log.error(String.format("Unknown Error Running Bulk Recharge %s Reason %s", id, ex.getMessage()));
        } finally {
            newBulkRechargeMapper.closeRequest(id);
        }
    }

    @SneakyThrows
    public void asyncRetryFailedRecharges(String id) {
        log.info("Submitting request to retry recharge failures for {}", id);
        jmsTemplate.convertAndSend(JMSConfig.RETRY_RECHARGE_QUEUE, objectMapper.writeValueAsString(id));
    }

    public void retryFailedRecharges(String id) {
        List<IndividualRequest> requests = newBulkRechargeMapper.findBulkIndividualFailedRequests(id);

        if (requests != null && !requests.isEmpty()) {
            final String message = String.format("Processing Failed Recharges for %s, size %d", id, requests.size());
            log.info(message);
            bulkRetryRecharge.retryRequestsWithoutPayment(requests);
        } else {
            log.info("Request to retry bulk recharges for {} is empty nothing todo", id);
        }
    }

    public void refundFailedRecharges(String id) {
        bulkRefundRecharge.refundRecharges(id);
    }

    public void refundFailedRecharge(Integer id, String bulkRequestId) {
        bulkRefundRecharge.refundRecharge(id, bulkRequestId);
    }

    @SneakyThrows
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

        log.info(String.format("AsyncBulk Recharge: Sending message for Asynchronous processing of Bulk Recharge Request (%s)", id));
        jmsTemplate.convertAndSend(JMSConfig.NEW_BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(id));
    }

    public PagedDto<NewBulkRechargeRequestDto> getUserRechargesByAutoRequestId(String id, Integer pageNumber, Integer pageSize) {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("id", id);
        parameterMap.put("userId", K.getUserId());

        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findBulkRequestByAutoId(parameterMap);

        return createDto(requests);
    }

    public PagedDto<NewBulkRechargeRequestDto> getUserRecharges(String userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findBulkRequestByUserId(userId);

        return createDto(requests);
    }

    public PagedDto<IndividualRequestDto> getBulkIndividualRequests(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.findPagedBulkIndividualRequests(id);

        return createIndividualDto(requests);
    }

    // Search Individual Requests if a particular Bulk Recharge, searches can be made by
    // a combination of recipient, status or product all 3 can be submitted in one request
    public PagedDto<IndividualRequestDto> searchIndividual(SearchIndividualDto dto, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.searchIndividual(dto);

        return createIndividualDto(requests);
    }

    // Search Bulk Recharges by Id or Date or Both
    public PagedDto<NewBulkRechargeRequestDto> search(SearchBulkRechargeDto dto) {
        PageHelper.startPage(dto.getPageNumber(), dto.getPageSize());
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.search(dto);

        return createDto(requests);
    }

    // Search for Failed BulkRecharges
    public PagedDto<NewBulkRechargeRequestDto> adminFailedSearch(SearchBulkFailedRechargeDto dto, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.adminFailedSearch(dto);

        return createDto(requests);
    }

    public PagedDto<IndividualRequestDto> adminIndividualFailedSearch(SearchIndividualDto dto, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.searchFailedIndividual(dto);

        return createIndividualDto(requests);
    }

    // Search Bulk Recharge By Date
    public PagedDto<NewBulkRechargeRequestDto> searchByDate(Date date, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.searchByDate(new SearchByDate(date));

        return createDto(requests);
    }

    public StatusMessageDto retryFailedRecharge(Integer id) {
        return bulkRetryRecharge.retryIndividualRequestWithoutPayment(id);
    }

    public ResolveRechargeDto resolveRecharges(String id, ResolveRechargeDto dto) {
        dto.setRechargeId(id);
        dto.setResolvedBy(K.getUserName());

        return bulkResolveRecharge.resolveBulk(dto)
                .orElseThrow(() -> new RuntimeException(String.format("Error Resolving Bulk Requests %s, it might have been resolved, refunded or successfully re-tried", id)));
    }

    public ResolveRechargeDto resolveRecharge(Integer id, ResolveRechargeDto dto) {
        dto.setResolvedBy(K.getUserName());

        return bulkResolveRecharge.resolveIndividual(dto)
                .orElseThrow(() -> new RuntimeException(String.format("Error Resolving Individual Bulk Requests %s, it might have been resolved, refunded or successfully re-tried", id)));
    }

    public PagedDto<NewBulkRechargeRequestDto> getFailedRequests(Integer pageNumber, Integer pageSize){
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findFailedRequests();

        return createDto(requests);
    }

    public PagedDto<NewBulkRechargeRequestDto> getFailedUnresolvedRequests(Integer pageNumber, Integer pageSize){
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findFailedUnResolvedRequests();

        return createDto(requests);
    }

    public PagedDto<IndividualRequestDto> getFailedIndividuals(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.findFailedIndividuals(id);

        return createIndividualDto(requests);
    }

    public PagedDto<IndividualRequestDto> getFailedUnresolvedIndividuals(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.findFailedUnresolvedIndividuals(id);

        return createIndividualDto(requests);
    }

    public ByteArrayInputStream generateExcelFile(String id) {
        NewBulkRechargeRequest request = newBulkRechargeMapper.findBulkRechargeById(id);

        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequests(id);

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new ResourceNotFoundException("IndividualRequests", "id", id);
        }

        String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());
        final String title = String.format("BulkRecharge Request (%s) Created On (%s)", id, s);
        return excelWriter.bulkRequestToExcel(individualRequests, title);
    }

    private void sendReport(AsyncRechargeDto dto, NewBulkRechargeRequest request) {

        if (dto.getEmail() == null || dto.getName() == null) {
            log.info("Unable to Send E-mail for Bulk Transaction, No E-mail Address found");
            return;
        }

       String dateString = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());

        final String messageBody = String.format("Dear %s\n\n Please find attached report for your Bulk Recharge\nCarried out on %s\nCurrent Balance %.2f",
                dto.getName(), dateString, dto.getBalance());

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(messageBody)
                .to(dto.getEmail())
                .subject("Bulk Recharge Report")
                .build();

        InputStreamResource resource = new InputStreamResource(generateExcelFile(dto.getId()));
        File targetFile = new File(dto.getId() + ".xlsx");

        try {
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            FileSystemResource fileSystemResource = new FileSystemResource(targetFile);
            final String emailId = mailService.sendMailWithAttachment(fileSystemResource, mailMessageDto);

            Map<String, String> emailMap = new HashMap<>();
            emailMap.put("id", dto.getId());
            emailMap.put("emailId", emailId);
            newBulkRechargeMapper.setEmailId(emailMap);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private PagedDto<IndividualRequestDto> createIndividualDto(Page<IndividualRequest> requests) {
        PagedDto<IndividualRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(mapper.listIndividualToIndividualDto(requests.getResult()));
        return pagedDto;
    }

    private PagedDto<NewBulkRechargeRequestDto> createDto(Page<NewBulkRechargeRequest> requests) {
        PagedDto<NewBulkRechargeRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(mapper.listRechargeToRechargDto(requests.getResult()));
        return pagedDto;
    }

    @SneakyThrows
    private void saveTransaction(NewBulkRechargeRequest request) {
        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .requestId(request.getId())
                .serviceCost(request.getTotalServiceCost())
                .transactionDate(new Date().toString())
                .userId(request.getUserId())
                .recipient("Bulk")
                .build();

        jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
    }
}
