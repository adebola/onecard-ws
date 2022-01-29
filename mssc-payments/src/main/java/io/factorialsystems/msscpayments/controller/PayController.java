package io.factorialsystems.msscpayments.controller;

import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pay")
public class PayController {
    private final PaymentService paymentService;

    @PostMapping
    ResponseEntity<PaymentRequestDto> initializePayment(@Valid @RequestBody PaymentRequestDto dto) {
        return new ResponseEntity<>(paymentService.initializePayment(dto), HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void verifyPayment(@RequestParam(value = "trxref") String trxref,
                              @RequestParam(value = "reference") String reference) {

        paymentService.verifyPayment(reference);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequestDto> checkPayment(@PathVariable("id") String id) {

       PaymentRequestDto dto = paymentService.checkPaymentValidity(id);

       if (dto == null)
       {
           final String message = String.format("Payment not consumated, pending id (%s)", id);
           log.error(message);
           throw new RuntimeException(message);
       }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
