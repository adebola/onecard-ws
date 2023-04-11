package io.factorialsystems.msscprovider.service.model;

import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.security.Keycloak;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
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

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    public PaymentRequestDto initializePayment(NewBulkRechargeRequest request, Optional<String> alternateUserId) {

        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getTotalServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .paymentMode(request.getPaymentMode())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        String uri = "api/v1/payment";

        if (alternateUserId.isPresent()) { // Request From Async Request noLogin
            final String token = keycloak.getUserToken(request.getUserId());
            restTemplate.getInterceptors().add(new RestTemplateInterceptorWithToken(token));
        } else if (ProviderSecurity.getUserId() == null) { // Anonymous Login
            uri = "api/v1/pay";
        } else {
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        }

        PaymentRequestDto newDto =
                restTemplate.postForObject(baseLocalUrl + uri, dto, PaymentRequestDto.class);

        if (newDto != null) {
            request.setRedirectUrl(newDto.getRedirectUrl());
            request.setAuthorizationUrl(newDto.getAuthorizationUrl());

            return newDto;
        }

        throw new RuntimeException("Error Initializing Payment Please contact OneCard Support");
    }

    public void reversePayment(String id) {
        log.info(String.format("Reversing Payment %s", id));
    }

    public Boolean checkPayment(String id) {
        RestTemplate restTemplate = new RestTemplate();

        PaymentRequestDto dto
                = restTemplate.getForObject(baseLocalUrl + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }
}
