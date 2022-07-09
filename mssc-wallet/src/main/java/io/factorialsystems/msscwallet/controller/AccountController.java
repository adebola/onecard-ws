package io.factorialsystems.msscwallet.controller;

import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.service.AccountService;
import io.factorialsystems.msscwallet.utils.K;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    public ResponseEntity<?> findAccounts(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findAccounts(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDto getAccountBalance() {
        return accountService.findAccountBalance();
    }

    @PutMapping("/balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('Onecard_Admin')")
    public void updateAccountBalance(@PathVariable("id") String id, @Valid @RequestBody BalanceDto balanceDto) {
        accountService.fundWallet(id, balanceDto);
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
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findWalletFundings(K.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/wallet/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> getAdminWalletFunding(@PathVariable("id") String id,
                                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(accountService.findWalletFundings(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@Valid @RequestBody TransferFundsDto dto) {
        return new ResponseEntity<>(accountService.transferFunds(dto), HttpStatus.OK);
    }

//
//    @GetMapping("/provider/{id}")
//    public ResponseEntity<AccountDto> findAccountByProviderId(@PathVariable("id") String id) {
//        return new ResponseEntity<>(accountService.findAccountByProviderId(id), HttpStatus.OK);
//    }
//
//    @PutMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(@PathVariable("id") String id, @Valid @RequestBody AccountDto dto) {
//        accountService.updateAccount(id, dto);
//    }
//
//    @PutMapping("/balance/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void updateBalance(@PathVariable("id") String id, @Valid @RequestBody AccountDto dto) {
//        accountService.updateAccountBalance(id, dto);
//    }
}
