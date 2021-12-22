package io.factorialsystems.msscvoucher.web.controller;

import io.factorialsystems.msscvoucher.dto.out.MessageDto;
import io.factorialsystems.msscvoucher.service.VoucherService;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_NUMBER;
import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_SIZE;

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

    @GetMapping
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

    @GetMapping("/search")
    public ResponseEntity<?> searchVouchers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                            @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(voucherService.searchVouchers(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}/batch")
    public ResponseEntity<?> getVoucherByBatchId(@PathVariable("id") String id,
                                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(voucherService.getBatchVouchers(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(voucherService.findVoucherById(id), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable("id") Integer id, @Valid @RequestBody VoucherDto dto) {
        return new ResponseEntity<>(voucherService.updateVoucher(id, dto), HttpStatus.OK);
    }

    @GetMapping("/{id}/suspend")
    public ResponseEntity<?> suspendVoucher(@PathVariable("id") Integer id) {
        VoucherDto dto = voucherService.suspendVoucher(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("Error suspending Voucher %d maybe does not exist", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/unsuspend")
    public ResponseEntity<?> unsuspendVoucher(@PathVariable("id") Integer id) {
        VoucherDto dto = voucherService.unsuspendVoucher(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("Error Un-suspending Voucher %d maybe does not exist", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
