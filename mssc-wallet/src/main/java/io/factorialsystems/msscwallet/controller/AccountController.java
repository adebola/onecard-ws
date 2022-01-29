package io.factorialsystems.msscwallet.controller;

import io.factorialsystems.msscwallet.dto.AccountDto;
import io.factorialsystems.msscwallet.dto.BalanceDto;
import io.factorialsystems.msscwallet.dto.WalletRequestDto;
import io.factorialsystems.msscwallet.dto.WalletResponseDto;
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
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    public void updateAccountBalance(@PathVariable("id") String id, @Valid @RequestBody BalanceDto balanceDto) {
        accountService.updateAccountBalance(id, balanceDto);
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
