package io.factorialsystems.msscwallet.controller;

import io.factorialsystems.msscwallet.dto.MessageDto;
import io.factorialsystems.msscwallet.service.TransactionService;
import io.factorialsystems.msscwallet.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
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
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(transactionService.findOrganizationTransactionsByAccountId(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getMyTransactions( @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        String id = K.getUserId();

        if (id != null) {
            if (pageNumber == null || pageNumber < 0) {
                pageNumber = K.DEFAULT_PAGE_NUMBER;
            }

            if (pageSize == null || pageSize < 1) {
                pageSize = K.DEFAULT_PAGE_SIZE;
            }

            return new ResponseEntity<>(transactionService.findUserTransactions(id, pageNumber, pageSize), HttpStatus.OK);
        }

        return new ResponseEntity<>(new MessageDto("You must be logged in to use this resource"), HttpStatus.BAD_REQUEST);
    }
}
