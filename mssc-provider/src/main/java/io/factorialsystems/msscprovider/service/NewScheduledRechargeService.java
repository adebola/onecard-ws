package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.NewScheduledRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.ScheduledRechargeResponseDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.helper.PaymentHelper;
import io.factorialsystems.msscprovider.helper.TransactionHelper;
import io.factorialsystems.msscprovider.mapper.recharge.NewBulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.mapper.recharge.NewScheduledRechargeMapstructMapper;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.file.BulkRequestExcelWriter;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.FileUploader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewScheduledRechargeService {

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;
    private final FileUploader fileUploader;
    private final BulkRequestExcelWriter excelWriter;
    private final NewBulkRechargeMapper newBulkRechargeMapper;
    private final NewBulkRechargeService newBulkRechargeService;
    private final NewScheduledRechargeMapper newScheduledRechargeMapper;
    private final NewBulkRechargeMapstructMapper newBulkRechargeMapstructMapper;
    private final NewScheduledRechargeMapstructMapper newScheduledRechargeMapstructMapper;

    public void uploadRecharge(MultipartFile file, Date scheduledDate) {
        UploadFile uploadFile = fileUploader.uploadFile(file);
        ExcelReader excelReader = new ExcelReader(uploadFile);

        NewScheduledRechargeRequestDto newRequestDto = new NewScheduledRechargeRequestDto();
        newRequestDto.setRechargeType("bulk");
        newRequestDto.setPaymentMode("wallet");
        newRequestDto.setScheduledDate(scheduledDate);

        List<IndividualRequestDto> individualRequests = excelReader.readContents();

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new RuntimeException("Empty or Un-Populated Excel file");
        }

        newRequestDto.setRecipients(individualRequests);
        startRecharge(newRequestDto);
    }

    public ScheduledRechargeResponseDto startRecharge(NewScheduledRechargeRequestDto dto) {
        NewScheduledRechargeRequest request = newScheduledRechargeMapstructMapper.rechargeDtoToRecharge(dto);

        log.info("Scheduling a Recharge Service ScheduledDate: {} Type {}", dto.getScheduledDate().toString(), dto.getRechargeType());

        if (dto.getRecipients() == null || dto.getRecipients().isEmpty()) {
            final String errorMessage = "No Group or Recipients specified, nothing todo";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        PaymentHelper helper = PaymentHelper.builder()
                .url(baseLocalUrl)
                .cost(request.getTotalServiceCost())
                .paymentMode(request.getPaymentMode())
                .redirectUrl(request.getRedirectUrl())
                .build();

        PaymentRequestDto paymentRequestDto = helper.initializePayment();

        if (paymentRequestDto == null) {
            final String errorMessage = "Payment Initialization Error";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

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

        // Save Request
        String id = UUID.randomUUID().toString();
        request.setId(id);
        newScheduledRechargeMapper.save(request);

        request.getRecipients().forEach(recipient -> {
            recipient.setScheduledRequestId(id);
            recipient.setExternalRequestId(UUID.randomUUID().toString());
        });

        newScheduledRechargeMapper.saveRecipients(request.getRecipients());

        // Move SaveTransaction to the Payment Server for
        // Wallet Payment, Transaction should be created once payment goes through
        if (request.getPaymentMode().equals("wallet")) {
            TransactionHelper transactionHelper = TransactionHelper.builder()
                    .serviceId(null)
                    .amount(paymentRequestDto.getAmount())
                    .recipient(null)
                    .userId(request.getUserId())
                    .requestId(id)
                    .build();

            transactionHelper.saveTransaction();
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

    public PagedDto<NewScheduledRechargeRequestDto> searchByDate(Date date, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewScheduledRechargeRequest> requests = newScheduledRechargeMapper.searchByDate(new SearchByDate(date));

        return createDto(requests);
    }

    public Boolean finalizeScheduledRecharge(String id) {
        NewScheduledRechargeRequest request = newScheduledRechargeMapper.findById(id);

        PaymentHelper helper = PaymentHelper.builder()
                .url(baseLocalUrl)
                .build();

        if (request != null && request.getPaymentId() != null && helper.checkPayment(request.getPaymentId())) {
            TransactionHelper transactionHelper = TransactionHelper.builder()
                    .serviceId(null)
                    .amount(request.getTotalServiceCost())
                    .recipient(null)
                    .userId(request.getUserId())
                    .requestId(id)
                    .build();

            transactionHelper.saveTransaction();
            return true;
        }

        return false;
    }

    public PagedDto<NewScheduledRechargeRequestDto> getUserRecharges(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<NewScheduledRechargeRequest> requests = newScheduledRechargeMapper.findRequestByUserId(K.getUserId());

        return createDto(requests);
    }

    public PagedDto<IndividualRequestDto> getBulkIndividualRequests(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<IndividualRequest> requests = newBulkRechargeMapper.findPagedBulkIndividualRequestsByScheduleId(id);

        return createIndividualDto(requests);
    }

    public void runRecharges(List<NewScheduledRechargeRequest> requests) {

        requests.forEach(request -> {
            log.info(String.format("Processing Scheduled Recharge (%s)", request.getId()));

            newScheduledRechargeMapper.closeRequest(request.getId());

            PaymentHelper helper = PaymentHelper.builder()
                    .url(baseLocalUrl)
                    .build();

            if (request.getPaymentId() != null && helper.checkPayment(request.getPaymentId())) {
                NewBulkRechargeRequest newBulkRechargeRequest
                        = newScheduledRechargeMapstructMapper.ToBulkRechargeRequest(request);

                final String id = UUID.randomUUID().toString();
                newBulkRechargeRequest.setId(id);
                newBulkRechargeRequest.setScheduledRequestId(request.getId());
                newBulkRechargeMapper.saveBulkRecharge(newBulkRechargeRequest);

                Map<String, String> recipientMap = new HashMap<>();
                recipientMap.put("bulkId", id);
                recipientMap.put("scheduledId", request.getId());

                newScheduledRechargeMapper.setBulkRequestId(recipientMap);

                AsyncRechargeDto rechargeDto = AsyncRechargeDto.builder()
                        .id(id)
                        .email(request.getUserEmail())
                        .build();

                newBulkRechargeService.runBulkRecharge(rechargeDto);

            } else {
                final String errorMessage
                        = String.format("Error Processing Scheduled recharge Request (%s) Payment Failed, Closing Request", request.getId());
                log.error(errorMessage);
            }
        });
    }

    public ByteArrayInputStream generateExcelFile(String id) {
        NewScheduledRechargeRequest request = newScheduledRechargeMapper.findById(id);

        if (request == null) {
            throw new ResourceNotFoundException("NewScheduledRechargeRequest", "id", id);
        }

        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequestsByScheduleId(id);

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new ResourceNotFoundException("ScheduledRequest-IndividualRequests", "id", id);
        }

        String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedOn());

        final String title = String.format("ScheduledRecharge Request (%s) Created On (%s)", id, s);

        return excelWriter.bulkRequestToExcel(individualRequests, title);
    }

    private PagedDto<IndividualRequestDto> createIndividualDto(Page<IndividualRequest> requests) {
        PagedDto<IndividualRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(newBulkRechargeMapstructMapper.listIndividualToIndividualDto(requests.getResult()));
        return pagedDto;
    }

    private PagedDto<NewScheduledRechargeRequestDto> createDto(Page<NewScheduledRechargeRequest> requests) {
        PagedDto<NewScheduledRechargeRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(newScheduledRechargeMapstructMapper.listRechargeToRechargeDto(requests.getResult()));
        return pagedDto;
    }
}


