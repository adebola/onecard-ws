package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@CommonsLog
class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    public void testNoAuthPayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .redirectUrl("redirectUrl")
                .paymentMode("paystack")
                .build();

        PaymentRequestDto paymentRequestDto = paymentClient.initializePayment(dto);
        log.info(paymentRequestDto);
    }

    @Test
    public void checkPayment() {
        final String id = "002c2023-83e8-44a8-89e8-31f7c2357449";
        PaymentRequestDto paymentRequestDto = paymentClient.checkPayment(id);
        log.info(paymentRequestDto);
    }

    @Test
    public void testAuthPayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .redirectUrl("redirectUrl")
                .paymentMode("wallet")
                .build();

        // PaymentRequestDto paymentRequestDto = paymentClient.initializePayment(dto);
        //log.info(paymentRequestDto);
    }
}