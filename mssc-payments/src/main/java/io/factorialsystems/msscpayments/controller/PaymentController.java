package io.factorialsystems.msscpayments.controller;

import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.dto.RefundRequestDto;
import io.factorialsystems.msscpayments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequest> getPayment(@PathVariable("id") String id) {
        return new ResponseEntity<>(paymentService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/refund/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> refundPayment(@PathVariable("id") String id, @Valid @RequestBody RefundRequestDto dto) {
        return new ResponseEntity<>(paymentService.refundPayment(id, dto), HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<PaymentRequestDto> initializePayment(@Valid @RequestBody PaymentRequestDto dto) {
        return new ResponseEntity<>(paymentService.initializePayment(dto), HttpStatus.OK);
    }
}
