package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.mapper.recharge.NewBulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.service.file.BulkRequestExcelWriter;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
import io.factorialsystems.msscprovider.service.model.ServiceHelper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
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
    private final ParameterCache parameterCache;
    private final BulkRequestExcelWriter excelWriter;
    private final NewBulkRechargeMapstructMapper mapper;
    private final NewBulkRechargeMapper newBulkRechargeMapper;
    private final SingleRechargeService singleRechargeService;

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

        if (requestDto.getPaymentMode().equals("wallet") && requestDto.getStatus() != 200) {
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

            try {
                AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                        .id(requestId)
                        .email(K.getEmail())
                        .build();

                log.info(String.format("Sending message for Asynchronous processing of Bulk Recharge Request (%s)", requestId));
                jmsTemplate.convertAndSend(JMSConfig.NEW_BULK_RECHARGE_QUEUE, objectMapper.writeValueAsString(asyncRechargeDto));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
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

        NewBulkRechargeRequest request = Optional.ofNullable(newBulkRechargeMapper.findBulkRechargeById(id))
                .orElseThrow(() -> new ResourceNotFoundException("NewBulkRechargeRequest", "id", id));

        if (request.getClosed()) {
            final String errMessage = String.format("BulkRechargeRequest request is closed id (%s)", id);
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

        List<IndividualRequest> individualRequests = Optional.ofNullable(newBulkRechargeMapper.findBulkIndividualRequests(id))
                .orElseThrow(() -> new ResourceNotFoundException("IndividualRequests", "bulkId", id));

        if (individualRequests.isEmpty()) {
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

            sendReport(dto);
        } catch (Exception ex) {
            log.error(String.format("Unknown Error Running Bulk Recharge %s Reason %s", id, ex.getMessage()));
        } finally {
            newBulkRechargeMapper.closeRequest(id);
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

    public PagedDto<IndividualRequestDto> searchIndividual(SearchIndividualDto dto, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.searchIndividual(dto);

        return createIndividualDto(requests);
    }

    public PagedDto<NewBulkRechargeRequestDto> search(SearchBulkRechargeDto dto) {
        PageHelper.startPage(dto.getPageNumber(), dto.getPageSize());
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.search(dto);

        return createDto(requests);
    }

    public PagedDto<NewBulkRechargeRequestDto> searchByDate(Date date, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewBulkRechargeRequest> requests = newBulkRechargeMapper.searchByDate(new SearchByDate(date));

        return createDto(requests);
    }

    public StatusMessageDto retryFailedRecharge(Integer id) {
        int status = 300;
        String statusMessage = "Recharge Retry Failed";
        boolean successful = false;

        IndividualRequestQuery query = IndividualRequestQuery.builder()
                .id(id)
                .userId(K.getUserId())
                .build();

        IndividualRequest individualRequest = Optional.ofNullable(newBulkRechargeMapper.findIndividualRequestById(query))
                .orElseThrow(() -> new ResourceNotFoundException("BulkIndividualRequest", "id", String.valueOf(id)));

        if (!individualRequest.getFailed()) {
            throw new RuntimeException(String.format("Request %d did not fail, you can only retry failed recharges", id));
        }

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(individualRequest.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());

            SingleRechargeRequest singleRechargeRequest = SingleRechargeRequest.builder()
                    .serviceId(individualRequest.getServiceId())
                    .serviceCode(individualRequest.getServiceCode())
                    .serviceCost(individualRequest.getServiceCost())
                    .id(individualRequest.getExternalRequestId())
                    .recipient(individualRequest.getRecipient())
                    .productId(individualRequest.getProductId())
                    .bulkRequestId(individualRequest.getBulkRequestId())
                    .paymentMode("wallet")
                    .build();

            PaymentRequestDto paymentRequestDto = Optional.ofNullable(SingleRechargeService.initializePayment(singleRechargeRequest))
                    .orElseThrow(() -> new RuntimeException(String.format("Error Processing Payment for Retry %d", id)));

            if (paymentRequestDto.getStatus() != 200) {
                throw new RuntimeException(String.format("Error Processing Payment For Retry Request %d", id));
            }

            RechargeStatus rechargeStatus = recharge.recharge(singleRechargeRequest);

            if (rechargeStatus.getStatus() == HttpStatus.OK) {
                successful = true;
                status = 200;
                statusMessage = "Retry Successful";

                singleRechargeService.saveTransaction(singleRechargeRequest);
                newBulkRechargeMapper.setIndividualRequestSuccess(id);
            } else {
                statusMessage = statusMessage + " Reason: " + rechargeStatus.getMessage();
                helper.reversePayment(paymentRequestDto.getId());
            }

            IndividualRequestRetry requestRetry = IndividualRequestRetry.builder()
                    .bulkIndividualRequestId(id)
                    .successful(successful)
                    .paymentId(paymentRequestDto.getId())
                    .statusMessage(statusMessage)
                    .build();

            newBulkRechargeMapper.saveRequestRetry(requestRetry);
        }

        return StatusMessageDto.builder()
                .message(statusMessage)
                .status(status)
                .build();
    }

    public ByteArrayInputStream generateExcelFile(String id) {
        NewBulkRechargeRequest request = Optional.ofNullable(newBulkRechargeMapper.findBulkRechargeById(id))
                .orElseThrow(() -> new ResourceNotFoundException("BulkRechargeRequest", "id", id));

        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequests(id);

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new ResourceNotFoundException("IndividualRequests", "id", id);
        }

        String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());

        final String title = String.format("BulkRecharge Request (%s) Created On (%s)", id, s);

        return excelWriter.bulkRequestToExcel(individualRequests, title);
    }

    private void sendReport(AsyncRechargeDto dto) {

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body("Please find attached report for recent Bulk Recharge")
                .to(dto.getEmail())
                .subject("Recharge Report")
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
