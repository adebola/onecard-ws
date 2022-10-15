package io.factorialsystems.msscpayments.payment;

import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;

public interface Payment {
    PaymentRequestDto initializePayment(PaymentRequestDto dto);

    PaymentRequestDto checkPaymentValidity(PaymentRequest paymentRequest);

}
