package io.factorialsystems.msscwallet.controller;

import io.factorialsystems.msscwallet.dto.DateRangeDto;
import io.factorialsystems.msscwallet.dto.MessageDto;
import io.factorialsystems.msscwallet.service.TransactionService;
import io.factorialsystems.msscwallet.utils.Constants;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> getUserTransactions(@PathVariable("id") String id,
                                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(transactionService.findUserTransactions(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable("id") String id) {
        return new ResponseEntity<>(transactionService.findTransaction(id), HttpStatus.OK);
    }

    @GetMapping("/organization")
    public ResponseEntity<?> getOrganizationTransactions(@RequestParam(value = "id") String id,
                                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize ) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(transactionService.findOrganizationTransactionsByAccountId(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getMyTransactions( @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        String id = Security.getUserId();

        if (id != null) {
            if (pageNumber == null || pageNumber < 0) {
                pageNumber = Constants.DEFAULT_PAGE_NUMBER;
            }

            if (pageSize == null || pageSize < 1) {
                pageSize = Constants.DEFAULT_PAGE_SIZE;
            }

            return new ResponseEntity<>(transactionService.findUserTransactions(id, pageNumber, pageSize), HttpStatus.OK);
        }

        return new ResponseEntity<>(new MessageDto("You must be logged in to use this resource"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/print")
    public ResponseEntity<Resource>  printUserTransactions(@Valid @RequestBody DateRangeDto dto) {
        String fileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), "xlsx");

        InputStreamResource file = new InputStreamResource(transactionService.generateExcelTransactionFile(dto));
        log.info(String.format("Generated Transaction File %s", fileName));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
