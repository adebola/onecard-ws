package io.factorialsystems.msscpayments.service;

import io.factorialsystems.msscpayments.payment.PaystackHelper;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class PaystackHelperTest {

    @Autowired
    PaystackHelper paystackHelper;

    @Test
    void initializePayment() {
//        InitializeTransactionRequest request = new InitializeTransactionRequest();
//        request.setAmount(2000000);
//        request.setEmail("adeomoboya@gmail.com");
//        request.setCallback_url("https://onecard.factorialsystems.io");

//        var x = paystackService.initializeTransaction(request);
//        log.info(x);
    }

    @Test
    void verifyPayment() {

    }

    @Test
    void findById() {
    }

    @Test
    void checkPaymentValidity() {
//        String id = "06ecd557-6fa8-4c1f-bd55-633b8bbb5408";
//
//        PaymentRequestDto dto = paystackService.checkPaymentValidity(id);
//        assertNotNull(dto);
//        log.info(dto);
    }
}
