package io.factorialsystems.msscpayments.service;

import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.mapper.PaymentRequestMapper;
import io.factorialsystems.msscpayments.payment.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;
    private final PaymentRequestMapper requestMapper;

    public PaymentRequest findById(String id) {
        return paymentMapper.findById(id);
    }

    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {

        if (dto == null || dto.getPaymentMode() == null) {
            throw new RuntimeException("PaymentRequestDto or PaymentRequest.getPaymentMode is NULL, we should not be here");
        }

        return paymentFactory
                .getPaymentHelper(dto.getPaymentMode())
                .initializePayment(dto);
    }

    public void verifyPayment(String reference) {
        log.info(String.format("Verify Payment Reference (%s)", reference));
        paymentMapper.verifyByReference(reference);
    }

    public PaymentRequestDto checkPaymentValidity(String id) {
        PaymentRequest paymentRequest = paymentMapper.findById(id);

        if (paymentRequest == null) {
            throw new RuntimeException(String.format("Invalid Payment Request %s, Error Processing Payment", id));
        }

        if (paymentRequest.getVerified()) {
            return requestMapper.requestToDto(paymentRequest);
        }

        return paymentFactory.getPaymentHelper(paymentRequest.getPaymentMode())
                .checkPaymentValidity(paymentRequest);
    }
}
