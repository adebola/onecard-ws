package io.factorialsystems.msscpayments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.mapper.PaymentRequestMapper;
import io.factorialsystems.msscpayments.paystack.InitializeTransactionRequest;
import io.factorialsystems.msscpayments.paystack.InitializeTransactionResponse;
import io.factorialsystems.msscpayments.paystack.VerifyTransactionResponse;
import io.factorialsystems.msscpayments.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaystackService {

    @Value("${api.paystack.url}")
    private String paystackURL;

    @Value("${api.paystack.callback.url}")
    private String paystackCallbackURL;

    @Value("${paystack.secret}")
    private String paystackSecret;

    private final ObjectMapper objectMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentRequestMapper requestMapper;

    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {
        InitializeTransactionRequest txRequest = new InitializeTransactionRequest();
        int amount = (int)(dto.getAmount().doubleValue() * 100);

        txRequest.setAmount(amount);
        txRequest.setEmail(K.getEmail());

        if (dto.getRedirectUrl() != null) {
            txRequest.setCallback_url(dto.getRedirectUrl());
        } else {
            txRequest.setCallback_url(paystackCallbackURL);
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(paystackSecret);

        try {
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(txRequest), headers);
            InitializeTransactionResponse response = restTemplate
                    .postForObject(paystackURL + "/initialize", request, InitializeTransactionResponse.class);

            if (response != null) {
                String id = UUID.randomUUID().toString();

                PaymentRequest paymentRequest = PaymentRequest.builder()
                        .id(id)
                        .amount(dto.getAmount())
                        .status(response.getStatus())
                        .message(response.getMessage())
                        .authorizationUrl(response.getData().getAuthorization_url())
                        .redirectUrl(dto.getRedirectUrl())
                        .accessCode(response.getData().getAccess_code())
                        .reference(response.getData().getReference())
                        .build();

                paymentMapper.save(paymentRequest);

                return PaymentRequestDto.builder()
                        .id(id)
                        .amount(dto.getAmount())
                        .authorizationUrl(response.getData().getAuthorization_url())
                        .redirectUrl(dto.getRedirectUrl())
                        .verified(false)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PaymentRequestDto checkPaymentValidity(String id) {
        PaymentRequest paymentRequest = paymentMapper.findById(id);

        if (paymentRequest == null) {
            throw new RuntimeException(String.format("Invalid Payment Request %s, Error Processing Payment", id));
        }

        if (paymentRequest.getVerified()) {
            return requestMapper.requestToDto(paymentRequest);
        }

        // Status not updated yet, check directly with Paystack
        return checkPaymentRemoteValidity(paymentRequest);
    }

    public void verifyPayment(String reference) {
        log.info(String.format("Verify PAYSTACK Payment Reference %s", reference));
        paymentMapper.verifyByReference(reference);
    }

    public PaymentRequest findById(String id) {
        return paymentMapper.findById(id);
    }

    private PaymentRequestDto checkPaymentRemoteValidity(PaymentRequest paymentRequest) {
        if (paymentRequest != null) {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(paystackSecret);
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<VerifyTransactionResponse> response =
                    restTemplate.exchange (
                            paystackURL + "/verify/" + paymentRequest.getReference(),
                            HttpMethod.GET,
                            request,
                            VerifyTransactionResponse.class
                    );

            VerifyTransactionResponse transactionResponse = response.getBody();

            if (transactionResponse != null) {
                boolean verified = transactionResponse.getData().getStatus().equals("success");

                if (verified) {
                    paymentMapper.verifyById(paymentRequest.getId());
                }

                return PaymentRequestDto.builder()
                        .id(paymentRequest.getId())
                        .verified(verified)
                        .amount(new BigDecimal(transactionResponse.getData().getAmount()))
                        .build();
            }
        }

        return null;
    }
}
