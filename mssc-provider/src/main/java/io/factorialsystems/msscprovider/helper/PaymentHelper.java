package io.factorialsystems.msscprovider.helper;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import io.factorialsystems.msscprovider.external.client.PaymentClient;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        PaymentClient paymentClient = ApplicationContextProvider.getBean(PaymentClient.class);

        if (ProviderSecurity.getUserId() == null) {
            return paymentClient.initializePayment(dto);
        }

        return paymentClient.makePayment(dto);
    }

    public Boolean checkPayment(String id) {
        PaymentClient paymentClient = ApplicationContextProvider.getBean(PaymentClient.class);
        PaymentRequestDto dto = paymentClient.checkPayment(id);
        return dto != null ? dto.getVerified() : false;
    }
}
