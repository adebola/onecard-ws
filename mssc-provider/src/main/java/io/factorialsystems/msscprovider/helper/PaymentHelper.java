package io.factorialsystems.msscprovider.helper;

import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.K;
import lombok.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHelper {
    private  String url;
    private  BigDecimal cost;
    private  String redirectUrl;
    private  String paymentMode;

    public PaymentRequestDto initializePayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(cost)
                .redirectUrl(redirectUrl)
                .paymentMode(paymentMode)
                .build();

        String uri;
        RestTemplate restTemplate = new RestTemplate();

        if (K.getUserId() == null) { // Anonymous Login
            uri = "api/v1/pay";
        } else {
            uri = "api/v1/payment";
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        }

        return restTemplate.postForObject(url + uri, dto, PaymentRequestDto.class);
    }

    public Boolean checkPayment(String id) {
        RestTemplate restTemplate = new RestTemplate();

        PaymentRequestDto dto
                = restTemplate.getForObject(url + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }
}
