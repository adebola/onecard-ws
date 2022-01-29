package io.factorialsystems.msscpayments.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.mapper.PaymentRequestMapper;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionRequest;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionResponse;
import io.factorialsystems.msscpayments.security.RestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletHelper implements Payment {
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentRequestMapper paymentRequestMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    private static final Integer SUCCESS = 200;
    private static final Integer INSUFFICIENT_BALANCE = 300;

    @Override
    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {
        String id = UUID.randomUUID().toString();

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .amount(dto.getAmount())
                .paymentMode(dto.getPaymentMode())
                .build();

        paymentMapper.save(paymentRequest);

        InitializeWalletTransactionRequest txRequest = InitializeWalletTransactionRequest.builder()
                .requestId(id)
                .amount(dto.getAmount())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(txRequest), headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
            InitializeWalletTransactionResponse response =
                    restTemplate.postForObject(baseUrl + "/api/v1/account", request, InitializeWalletTransactionResponse.class);

            if (response == null) {
                final String message = String.format("RuntimeException charging Wallet Payment ID %s", id);
                log.error(message);
                throw new RuntimeException(message);
            }

            Integer status = response.getStatus();

            if (Objects.equals(status, SUCCESS)) {
                paymentRequest.setMessage("SUCCESS");
                paymentRequest.setStatus(status);
                paymentRequest.setVerified(true);
                paymentRequest.setPaymentVerified(new Timestamp(System.currentTimeMillis()));

                paymentMapper.update(paymentRequest);
                log.info(String.format("Successful Wallet Payment for (%s) Amount (%.2f)", paymentRequest.getId(), paymentRequest.getAmount().doubleValue()));

            } else if (Objects.equals(status, INSUFFICIENT_BALANCE)) {
                paymentRequest.setMessage("INSUFFICIENT FUNDS");
                paymentRequest.setStatus(status);
                paymentRequest.setVerified(false);

                paymentMapper.update(paymentRequest);
                log.error(String.format("UnSuccessful Wallet Payment for (%s) Amount (%.2f) Insufficient Funds", paymentRequest.getId(), paymentRequest.getAmount().doubleValue()));
            } else {
                log.error("Error Status in Payment");
                throw new RuntimeException("Invalid Wallet Payment Status");
            }

        } catch (JsonProcessingException jex) {
            log.error(jex.getMessage());
            throw new RuntimeException(jex.getMessage());
        }

        return paymentRequestMapper.requestToDto(paymentRequest);
    }

    @Override
    public PaymentRequestDto checkPaymentValidity(PaymentRequest paymentRequest) {
        return null;
    }
}
