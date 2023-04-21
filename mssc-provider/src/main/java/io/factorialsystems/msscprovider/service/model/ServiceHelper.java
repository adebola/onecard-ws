package io.factorialsystems.msscprovider.service.model;

import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.external.client.PaymentClient;
import io.factorialsystems.msscprovider.security.Keycloak;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptorWithToken;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceHelper {
    private final Keycloak keycloak;
    private final PaymentClient paymentClient;

    @Value("${api.local.host.baseurl}")
    private String baseUrl;

    public PaymentRequestDto initializePayment(NewBulkRechargeRequest request, Optional<String> alternateUserId) {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getTotalServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .paymentMode(request.getPaymentMode())
                .build();

        PaymentRequestDto newDto = null;

        if (alternateUserId.isPresent()) { // Request From Async Request noLogin
            final String uri = "api/v1/payment";
            final String token = keycloak.getUserToken(request.getUserId());
            RestTemplate restTemplate = new RestTemplate();

            if (token == null) {
                final String message = String.format("Unable to acquire token for Alternate UserId %s", alternateUserId.get());
                log.error(message);
                throw new RuntimeException(message);
            }

            restTemplate.getInterceptors().add(new RestTemplateInterceptorWithToken(token));
            newDto = restTemplate.postForObject(baseUrl + uri, dto, PaymentRequestDto.class);
        } else if (ProviderSecurity.getUserId() == null) { // Anonymous Login
            newDto = paymentClient.initializePayment(dto);
        } else {
            newDto = paymentClient.makePayment(dto);
        }

        if (newDto == null) {
            throw new RuntimeException("Error Initializing Payment Please contact OneCard Support");
        }

        request.setRedirectUrl(newDto.getRedirectUrl());
        request.setAuthorizationUrl(newDto.getAuthorizationUrl());
        return newDto;
    }

    public Boolean checkPayment(String id) {
        PaymentRequestDto dto = paymentClient.checkPayment(id);
        return dto != null ? dto.getVerified() : false;
    }
}
