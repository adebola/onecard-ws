package io.factorialsystems.msscvoucher.web.controller;

import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.service.VoucherService;
import io.factorialsystems.msscvoucher.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_SIZE;
import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_NUMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voucher")
public class VoucherController {

    private final Environment environment;
    private final VoucherService voucherService;

    @GetMapping("/status")
    public String status() {
        final String status = K.SERVICE_STATUS + environment.getProperty("local.server.port");

        log.info(status);
        return status;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllVouchers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return new ResponseEntity<>(voucherService.findAllVouchers(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(voucherService.findVoucherById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoucher(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(voucherService.deleteVoucher(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/denomination")
    public ResponseEntity<String> changeVoucherDenomination(@PathVariable("id") Integer id, @RequestBody VoucherChangeRequest request) {

        if (request != null || request.getDenomination() != null && request.getDenomination() > 0.0) {
            return new ResponseEntity<>(voucherService.changeVoucherDenomination(id, request), HttpStatus.OK);
        }

        return new ResponseEntity<>("Bad Arguments Denomination must be submitted", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/expiry")
    public ResponseEntity<String> changeVoucherExpiry(@PathVariable("id") Integer id, @RequestBody VoucherChangeRequest request) {

        if (request != null || request.getExpiryDate() != null) {
            return new ResponseEntity<>(voucherService.changeVoucherExpiry(id, request), HttpStatus.OK);
        }

        return new ResponseEntity<>("Bad Arguments Expiry Date must be submitted", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateVoucher(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(voucherService.activateVoucher(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deActivateVoucher(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(voucherService.deActivateVoucher(id), HttpStatus.OK);
    }
}
