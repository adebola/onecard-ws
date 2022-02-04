package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.ScheduledRechargeMapper;
import io.factorialsystems.msscprovider.domain.BulkRecipient;
import io.factorialsystems.msscprovider.domain.rechargerequest.BulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.mapper.recharge.BulkRechargeMapstructMapper;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.mapper.recharge.ScheduledRechargeMapstructMapper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledRechargeService {
    private static final String BULK_RECHARGE = "bulk";
    private static final String SINGLE_RECHARGE = "single";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final RechargeMapstructMapper rechargeMapstructMapper;
    private final ScheduledRechargeMapper scheduledRechargeMapper;
    private final BulkRechargeMapstructMapper bulkRechargeMapstructMapper;
    private final ScheduledRechargeMapstructMapper scheduledRechargeMapstructMapper;

    public ScheduledRechargeRequestResponseDto startRecharge(ScheduledRechargeRequestDto dto) {
        ScheduledRechargeRequest request = scheduledRechargeMapstructMapper.rechargeDtoToRecharge(dto);

        if (dto.getRechargeType().equals(SINGLE_RECHARGE)) {
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

                return ScheduledRechargeRequestResponseDto.builder()
                        .authorizationUrl(paymentRequestDto.getAuthorizationUrl())
                        .redirectUrl(paymentRequestDto.getRedirectUrl())
                        .paymentMode(paymentRequestDto.getPaymentMode())
                        .message(paymentRequestDto.getMessage())
                        .status(paymentRequestDto.getStatus())
                        .id(id)
                        .build();

            }
        } else if (dto.getRechargeType().equals(BULK_RECHARGE)) {

            if (dto.getRecipients() == null && dto.getGroupId() == null) {
                log.error("No Group or Recipients specified, nothing todo");
                return null;
            }

            BulkRechargeRequestDto bulkRechargeRequestDto = bulkRechargeMapstructMapper.scheduledToBulk(dto);
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

            return ScheduledRechargeRequestResponseDto.builder()
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

    private SingleRechargeRequest checkSingleRequestParameters(ScheduledRechargeRequestDto dto) {
        SingleRechargeRequestDto singleRechargeRequestDto = rechargeMapstructMapper.scheduledToSingle(dto);
        SingleRechargeRequest request =
                rechargeMapstructMapper.rechargeDtoToRecharge(singleRechargeRequestDto);
        return SingleRechargeService.checkParameters(request, singleRechargeRequestDto) ? request : null;
    }


    private BulkRechargeRequest checkBulkRequestParameters(ScheduledRechargeRequestDto dto) {

        if (dto.getRecipients() == null && dto.getGroupId() == null) {
            log.error("No Group or Recipients specified, nothing todo");
            return null;
        }

        BulkRechargeRequestDto bulkRechargeRequestDto = bulkRechargeMapstructMapper.scheduledToBulk(dto);
        return bulkRechargeMapstructMapper.rechargeDtoToRecharge(bulkRechargeRequestDto);
    }

    private void saveTransaction(ScheduledRechargeRequest request, BigDecimal amount) {

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(request.getServiceId())
                .requestId(request.getId())
                .serviceCost(amount)
                .transactionDate(new Date().toString())
                .userId(K.getUserId())
                .recipient(request.getRecipient() == null ? "scheduled-bulk" : request.getRecipient())
                .build();
        try {
            jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
        } catch (JsonProcessingException e) {
            log.error("Error sending JMS Transaction Message to Wallet service {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
