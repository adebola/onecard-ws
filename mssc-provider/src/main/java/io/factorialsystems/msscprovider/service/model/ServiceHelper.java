package io.factorialsystems.msscprovider.service.model;

import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.PaymentRequestDto;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ServiceHelper {

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    public PaymentRequestDto initializePayment(NewBulkRechargeRequest request) {

        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(request.getTotalServiceCost())
                .redirectUrl(request.getRedirectUrl())
                .paymentMode(request.getPaymentMode())
                .build();

        String uri;
        RestTemplate restTemplate = new RestTemplate();

        if (K.getUserId() == null) { // Anonymous Login
            uri = "api/v1/pay";
        } else {
            uri = "api/v1/payment";
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
