package io.factorialsystems.msscpayments.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.config.JMSConfig;
import io.factorialsystems.msscpayments.dto.AsyncRefundRequestDto;
import io.factorialsystems.msscpayments.dto.AsyncRefundResponseDto;
import io.factorialsystems.msscpayments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @JmsListener(destination = JMSConfig.PAYMENT_REFUND_QUEUE)
    public void listenForPaymentRefund(String jsonData) throws IOException {

        if (jsonData != null) {
            AsyncRefundRequestDto request = objectMapper.readValue(jsonData, AsyncRefundRequestDto.class);
            PaymentService paymentService = applicationContext.getBean(PaymentService.class);
            paymentService.refundPayment(request);
        }
    }

    @JmsListener(destination = JMSConfig.WALLET_REFUND_RESPONSE_QUEUE_PAYMENT)
    public void listenForPaymentRefundResponse(String jsonData) throws IOException {

        if (jsonData != null) {
            AsyncRefundResponseDto response = objectMapper.readValue(jsonData, AsyncRefundResponseDto.class);
            PaymentService paymentService = applicationContext.getBean(PaymentService.class);
            paymentService.refundPaymentResponse(response);
        }
    }
}
