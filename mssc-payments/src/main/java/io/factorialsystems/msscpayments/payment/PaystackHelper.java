package io.factorialsystems.msscpayments.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.payment.paystack.InitializeTransactionRequest;
import io.factorialsystems.msscpayments.payment.paystack.InitializeTransactionResponse;
import io.factorialsystems.msscpayments.payment.paystack.VerifyTransactionResponse;
import io.factorialsystems.msscpayments.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaystackHelper implements Payment {
    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;

    @Value("${api.paystack.url}")
    private String baseUrl;
    @Value("${api.paystack.callback.url}")
    private String callbackUrl;
    @Value("${paystack.secret}")
    private String secret;

    private static HttpHeaders headers;

    @Override
    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {
        InitializeTransactionRequest txRequest = new InitializeTransactionRequest();
        int amount = (int) (dto.getAmount().doubleValue() * 100);

        txRequest.setAmount(amount);
        txRequest.setEmail(Security.getEmail());

        if (dto.getRedirectUrl() != null) {
            txRequest.setCallback_url(dto.getRedirectUrl());
        } else {
            dto.setRedirectUrl(callbackUrl);
            txRequest.setCallback_url(callbackUrl);
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(txRequest), getHeader());
            InitializeTransactionResponse response = restTemplate
                    .postForObject(baseUrl + "/initialize", request, InitializeTransactionResponse.class);

            if (response != null) {
                String id = UUID.randomUUID().toString();

                PaymentRequest paymentRequest = PaymentRequest.builder()
                        .id(id)
                        .amount(dto.getAmount())
                        .message(response.getMessage())
                        .status(response.getStatus() ? 1 : 0)
                        .authorizationUrl(response.getData().getAuthorization_url())
                        .redirectUrl(dto.getRedirectUrl())
                        .accessCode(response.getData().getAccess_code())
                        .reference(response.getData().getReference())
                        .paymentMode(dto.getPaymentMode())
                        .build();

                paymentMapper.save(paymentRequest);

                log.info(String.format("paystack payment initialized successfully access_code (%s)", response.getData().getAccess_code()));

                return PaymentRequestDto.builder()
                        .id(id)
                        .status(response.getStatus() ? 200 : 300)
                        .message(response.getMessage())
                        .amount(dto.getAmount())
                        .authorizationUrl(response.getData().getAuthorization_url())
                        .redirectUrl(dto.getRedirectUrl())
                        .paymentMode(dto.getPaymentMode())
                        .verified(false)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PaymentRequestDto checkPaymentValidity(PaymentRequest paymentRequest) {
        if (paymentRequest != null) {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> request = new HttpEntity<>(getHeader());

            log.info("Checking Validity for paystack Payment {}, url {}, secret {}", paymentRequest.getReference(), baseUrl, secret);

            ResponseEntity<VerifyTransactionResponse> response =
                    restTemplate.exchange(
                            baseUrl + "/verify/" + paymentRequest.getReference(),
                            HttpMethod.GET,
                            request,
                            VerifyTransactionResponse.class
                    );

            VerifyTransactionResponse transactionResponse = response.getBody();

            if (transactionResponse != null) {
                boolean verified = transactionResponse.getData().getStatus().equals("success");

                if (verified) {
                    paymentMapper.verifyById(paymentRequest.getId());
                    log.info(String.format("Paystack Payment Request %s Verified", paymentRequest.getId()));
                } else {
                    log.info(String.format("Paystack Payment Request %s NOT-Verified", paymentRequest.getId()));
                }

                return PaymentRequestDto.builder()
                        .id(paymentRequest.getId())
                        .verified(verified)
                        .amount(new BigDecimal(transactionResponse.getData().getAmount()))
                        .build();
            }
        }

        log.error("Payment Validity Request Error");
        return null;
    }

    private HttpHeaders getHeader() {
        if (PaystackHelper.headers == null) {
            PaystackHelper.headers = new HttpHeaders();
            PaystackHelper.headers.setContentType(MediaType.APPLICATION_JSON);
            PaystackHelper.headers.setBearerAuth(secret);
        }

        return PaystackHelper.headers;
    }
}
