package io.factorialsystems.msscpayments.controller;

import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.service.PaystackService;
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
    private final PaystackService service;

    @PostMapping
    ResponseEntity<PaymentRequestDto> initializePayment(@Valid @RequestBody PaymentRequestDto dto) {
        return new ResponseEntity<>(service.initializePayment(dto), HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void verifyPayment(@RequestParam(value = "trxref") String trxref,
                              @RequestParam(value = "reference") String reference) {

        service.verifyPayment(reference);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentRequestDto> checkPayment(@PathVariable("id") String id) {
        return new ResponseEntity<>(service.checkPaymentValidity(id), HttpStatus.OK);
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public void test(@RequestParam(value = "trxref") String trxref,
                     @RequestParam(value = "reference") String reference) {
       log.info(String.format("Txref %s and Referebce %s", trxref, reference));
    }
}
