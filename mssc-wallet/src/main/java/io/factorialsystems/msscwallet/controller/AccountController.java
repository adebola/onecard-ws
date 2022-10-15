package io.factorialsystems.msscwallet.controller;

import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.service.AccountService;
import io.factorialsystems.msscwallet.service.AdjustmentService;
import io.factorialsystems.msscwallet.utils.Constants;
import io.factorialsystems.msscwallet.utils.Security;
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
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;
    private final AdjustmentService adjustmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    public ResponseEntity<?> findAccounts(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findAccounts(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDto getAccountBalance() {
        return accountService.findAccountBalance();
    }

    @PutMapping("/balance/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> updateAccountBalance(@PathVariable("id") String id, @Valid @RequestBody BalanceDto balanceDto) {
        return new ResponseEntity<>( accountService.fundWallet(id, balanceDto), HttpStatus.OK);
    }

    @PutMapping("/refund/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<RefundResponseDto> refundWallet(@PathVariable("id") String id, @Valid @RequestBody RefundRequestDto refundRequestDto) {
        return new ResponseEntity<>(accountService.asyncRefundWallet(id, refundRequestDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> findAccountById(@PathVariable("id") String id) {
        return new ResponseEntity<>(accountService.findAccountById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<AccountDto> findAccountByUserId(@PathVariable("id") String id) {
        return new ResponseEntity<>(accountService.findAccountByUserId(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WalletResponseDto> charge(@RequestBody @Valid WalletRequestDto request) {
        return new ResponseEntity<>(accountService.chargeAccount(request), HttpStatus.OK);
    }

    @PostMapping("/fund")
    public ResponseEntity<FundWalletResponseDto> initializeFundWallet(@RequestBody @Valid FundWalletRequestDto request) {
        return new ResponseEntity<>(accountService.initializeFundWallet(request), HttpStatus.OK);
    }

    @GetMapping("/fund/{id}")
    public ResponseEntity<MessageDto> fundWallet(@PathVariable("id") String id) {
        return new ResponseEntity<>(accountService.fundWallet(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<AccountDto> createOrganizationAccount(@Valid @RequestBody CreateAccountDto dto) {
        return new ResponseEntity<>(accountService.createAccount(dto), HttpStatus.CREATED);
    }

    @GetMapping("/wallet")
    public ResponseEntity<?> getWalletFunding(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findWalletFunding(Security.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/wallet/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> getAdminWalletFunding(@PathVariable("id") String id,
                                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findWalletFunding(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@Valid @RequestBody TransferFundsDto dto) {
        return new ResponseEntity<>(accountService.transferFunds(dto), HttpStatus.OK);
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> adjustAccount(@Valid @RequestBody AdjustmentRequestDto adjustmentRequestDto) {
        return  new ResponseEntity<>(adjustmentService.adjustBalance(adjustmentRequestDto), HttpStatus.OK);
    }
}
