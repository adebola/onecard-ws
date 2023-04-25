package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.config.CachingConfig;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;

@SpringBootTest
@CommonsLog
class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private ImpersonatePaymentClient impersonatePaymentClient;

    @Autowired
    CacheManager cacheManager;

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

    @Test
    public void testImpersonatePayment() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";

        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .redirectUrl("redirectUrl")
                .paymentMode("wallet")
                .build();

        Cache cache = cacheManager.getCache(CachingConfig.ALTERNATE_USER_ID);

        if (cache != null) {
            cache.evictIfPresent("user");
            cache.put("user", id);
            PaymentRequestDto paymentRequestDto = impersonatePaymentClient.makePayment(dto);
            log.info(paymentRequestDto);
        }

    }
}