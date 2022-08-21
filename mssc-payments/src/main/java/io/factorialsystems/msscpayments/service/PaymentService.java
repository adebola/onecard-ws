package io.factorialsystems.msscpayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.config.JMSConfig;
import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.domain.RefundRequest;
import io.factorialsystems.msscpayments.dto.*;
import io.factorialsystems.msscpayments.exception.ResourceNotFoundException;
import io.factorialsystems.msscpayments.mapper.PaymentRequestMapper;
import io.factorialsystems.msscpayments.payment.PaymentFactory;
import io.factorialsystems.msscpayments.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;
    private final PaymentRequestMapper requestMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    public PaymentRequest findById(String id) {
        return paymentMapper.findById(id);
    }

    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {

        if (dto == null || dto.getPaymentMode() == null) {
            throw new RuntimeException("PaymentRequestDto or PaymentRequest.getPaymentMode is NULL, we should not be here");
        }

        return paymentFactory
                .getPaymentHelper(dto.getPaymentMode())
                .initializePayment(dto);
    }

    public void verifyPayment(String reference) {
        log.info(String.format("Verify Payment Reference (%s)", reference));
        paymentMapper.verifyByReference(reference);
    }

    public PaymentRequestDto checkPaymentValidity(String id) {
        PaymentRequest paymentRequest = paymentMapper.findById(id);

        if (paymentRequest == null) {
            throw new RuntimeException(String.format("Invalid Payment Request %s, Error Processing Payment", id));
        }

        if (paymentRequest.getVerified()) {
            return requestMapper.requestToDto(paymentRequest);
        }

        return paymentFactory.getPaymentHelper(paymentRequest.getPaymentMode())
                .checkPaymentValidity(paymentRequest);
    }

    @SneakyThrows
    public void refundPayment(AsyncRefundRequestDto request) {
        PaymentRequest paymentRequest = Optional.ofNullable(paymentMapper.findById(request.getPaymentId()))
                .orElseThrow(() -> new ResourceNotFoundException("payment", "id", request.getPaymentId()));

        log.info(String.format("Refunding %.2f", request.getAmount()));

        if (paymentRequest.getVerified() && paymentRequest.getStatus() == 200) {
            Double aDouble = paymentMapper.findRefundTotalByPaymentId(request.getPaymentId());
            BigDecimal totalRefunded = BigDecimal.valueOf(aDouble == null ? 0 : aDouble);

            if (totalRefunded.add(request.getAmount()).compareTo(request.getAmount()) > 0) {
                final String message =
                        String.format("Error Unable to perform refund, cumulative refunds on payment %s is %.2f cannot accommodate extra %.2f",
                                request.getPaymentId(), totalRefunded, request.getAmount());
                log.error(message);

                return;
            }

            log.info(String.format("MQ reversing %s Payment Id %s for %.2f", paymentRequest.getPaymentMode(), paymentRequest.getId(), paymentRequest.getAmount()));
            jmsTemplate.convertAndSend(JMSConfig.WALLET_REFUND_QUEUE, objectMapper.writeValueAsString(request));
        }
    }

    public void refundPaymentResponse(AsyncRefundResponseDto dto) {
        RefundRequest refundRequest = RefundRequest.builder()
                .id(UUID.randomUUID().toString())
                .paymentId(dto.getPaymentId())
                .refundedBy(dto.getUserId())
                .amount(dto.getAmount())
                .fundRequestId(dto.getId())
                .build();

        log.info("Update Payment Refund {}", dto.getPaymentId());

        paymentMapper.saveRefundRequest(refundRequest);
    }

    @SneakyThrows
    public RefundResponseDto refundPayment(String id, RefundRequestDto refundRequestDto) {
        PaymentRequest request = Optional.ofNullable(paymentMapper.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("payment", "id", id));

        if (request.getVerified() && request.getStatus() == 200) {
            Double aDouble = paymentMapper.findRefundTotalByPaymentId(id);
            BigDecimal totalRefunded = BigDecimal.valueOf(aDouble == null ? 0 : aDouble);

            if (totalRefunded.add(refundRequestDto.getAmount()).compareTo(request.getAmount()) > 0) {
                final String message =
                        String.format("Error Unable to perform refund, cumulative refunds on payment %s is %.2f cannot accommodate an extra %.2f",
                                id, totalRefunded, refundRequestDto.getAmount());
                log.error(message);

                return RefundResponseDto.builder()
                        .message(message)
                        .status(300)
                        .build();
            }

            log.info(String.format("Refunding %s Payment Id %s for %.2f", request.getPaymentMode(), request.getId(), request.getAmount()));

            final String accessToken = Optional.ofNullable(K.getAccessToken())
                    .orElseThrow(() -> new RuntimeException("No Access Token User Must be logged On"));

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> httpRequest = new HttpEntity<>(objectMapper.writeValueAsString(refundRequestDto), headers);

            ResponseEntity<RefundResponseDto> response
                    = restTemplate.exchange (baseUrl + "/api/v1/account/refund/" + refundRequestDto.getUserId(),
                                                HttpMethod.PUT, httpRequest, RefundResponseDto.class);

            RefundResponseDto dto = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException(String.format("Error Refund Wallet for Payment %s, Amount %.2f", id, refundRequestDto.getAmount())));

            RefundRequest refundRequest = RefundRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentId(request.getId())
                    .refundedBy(K.getUserName())
                    .amount(refundRequestDto.getAmount())
                    .fundRequestId(dto.getId())
                    .build();

            paymentMapper.saveRefundRequest(refundRequest);

            return dto;
        }

        return RefundResponseDto.builder()
                .status(300)
                .message(String.format(String.format("Error refunding Payment %s Payment might not have been fulfilled", id)))
                .build();
    }
}
