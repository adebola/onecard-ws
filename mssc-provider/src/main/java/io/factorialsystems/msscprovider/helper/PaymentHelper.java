package io.factorialsystems.msscprovider.helper;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.properties.GeneralProperties;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHelper {
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

        if (ProviderSecurity.getUserId() == null) { // Anonymous Login
            uri = "api/v1/pay";
        } else {
            uri = "api/v1/payment";
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        }

        final String url = ApplicationContextProvider.getBean(GeneralProperties.class).getBaseUrl();

        return restTemplate.postForObject( url + uri, dto, PaymentRequestDto.class);
    }

    public Boolean checkPayment(String id) {
        RestTemplate restTemplate = new RestTemplate();

        final String url = ApplicationContextProvider.getBean(GeneralProperties.class).getBaseUrl();

        PaymentRequestDto dto
                = restTemplate.getForObject(url + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }
}
