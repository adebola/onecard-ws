package io.factorialsystems.msscvoucher.web.controller;

import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.service.BatchService;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_NUMBER;
import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch")
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<?> getBatches(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.getAllBatches(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBatches(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                           @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.searchBatches(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchDto> getBatch(@PathVariable("id") String id) {
        return new ResponseEntity<>(batchService.getBatch(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/vouchers")
    public ResponseEntity<?> getBatchVouchers(@PathVariable("id") String id,
                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.getBatchVouchers(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BatchDto> generateBatchVouchers(@Valid @RequestBody BatchDto request) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        String userName = (String) claims.get("name");

        return  new ResponseEntity<>(batchService.generateBatch(userName, request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchDto> updateVoucherBatch(@PathVariable("id") String id, @Valid @RequestBody BatchDto batchDto) {
        return new ResponseEntity<>(batchService.updateBatch(id, batchDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoucherBatch(@PathVariable("id") String id) {
        return new ResponseEntity<>(batchService.deleteVoucherBatch(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/denomination")
    public ResponseEntity<String> changeVoucherBatchDenomination(@PathVariable("id") String id, @RequestBody VoucherChangeRequest request) {

        if (request != null || request.getDenomination() != null && request.getDenomination() > 0.0) {
            return new ResponseEntity<>(batchService.changeVoucherBatchDenomination(id, request.getDenomination()), HttpStatus.OK);
        }

        return new ResponseEntity<>("Bad Arguments Denomination must be submitted", HttpStatus.BAD_REQUEST);

    }

    @PutMapping("/{id}/expiry")
    public ResponseEntity<String> changeVoucherBatchExpiry(@PathVariable("id") String id, @RequestBody VoucherChangeRequest request) {

        if (request != null || request.getExpiryDate() != null) {
            return new ResponseEntity<>(batchService.changeVoucherBatchExpiry(id, request), HttpStatus.OK);
        }

        return new ResponseEntity<>("Bad Arguments Expiry Date must be submitted", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateVoucherBatch(@PathVariable("id") String id) {
        return new ResponseEntity<>(batchService.activateVoucherBatch(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deActivateVoucherBatch(@PathVariable("id") String id) {
        return new ResponseEntity<>(batchService.deActivateVoucherBatch(id), HttpStatus.OK);
    }
}
