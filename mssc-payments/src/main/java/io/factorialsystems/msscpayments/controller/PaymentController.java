package io.factorialsystems.msscpayments.controller;

import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.service.PaystackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaystackService paystackService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequest> getPayment(@PathVariable("id") String id) {
        return new ResponseEntity<>(paystackService.findById(id), HttpStatus.OK);
    }
}
