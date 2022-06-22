package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dto.AccountDto;
import io.factorialsystems.msscwallet.dto.BalanceDto;
import io.factorialsystems.msscwallet.dto.FundWalletRequestDto;
import io.factorialsystems.msscwallet.dto.WalletRequestDto;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import io.factorialsystems.msscwallet.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@CommonsLog
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Test
    void findAccounts() {
        var accounts = accountService.findAccounts(1, 20);
        assert (accounts != null);
        assert (accounts.getTotalSize() > 0);
    }

    @Test
    void findWrongAccountById() {

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            String id = "wrong-id";

            AccountDto account = accountService.findAccountById(id);
            assert (account != null);
            assertEquals(id, account.getId());

            log.info(account);
        });

        log.info(exception.getMessage());
    }

    @Test
    void findAccountById() {
        final String id = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";

        AccountDto account = accountService.findAccountById(id);
        assertNotNull (account);
        assertEquals(id, account.getId());

        log.info(account);
    }


    @Test
    void findAccountByUserId() {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            String userId = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
            AccountDto dto = accountService.findAccountByUserId(userId);
            assertNotNull (dto);
        }
    }

    @Test
    void updateAccountBalance() {

        final String id = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
        BigDecimal addition = new BigDecimal(1000);

        AccountDto dto = accountService.findAccountById(id);
        assertNotNull (dto);
        log.info(String.format("Current Account Balance Before %.2f", dto.getBalance()));

        BalanceDto balanceDto = new BalanceDto(addition);
        accountService.updateAccountBalance(id, balanceDto);

        AccountDto newDto = accountService.findAccountById(id);
        assertEquals(dto.getBalance().add(addition), newDto.getBalance());
        log.info(String.format("New Account Balance Before %.2f", newDto.getBalance()));
    }

    @Test
    void findAccountBalance() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            var balance = accountService.findAccountBalance();
            assertNotNull (balance);
            log.info(balance.getBalance());
        }
    }

    @Test
    void initializeFundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            FundWalletRequestDto request = new FundWalletRequestDto();
            request.setAmount(new BigDecimal(1500));

            var response = accountService.initializeFundWallet(request);
            assertNotNull (response);
            log.info(response.getAuthorizationUrl());
        }
    }

    @Test
    void fundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String transactionId = "897dc52c-79ef-4bd6-b5d0-57367ba0c4b0";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            var response = accountService.fundWallet(transactionId);
            assertNotNull (response);
            log.info(response);
        }
    }

    @Test
    void chargeAccount() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final int amount = 200;

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            var balance = accountService.findAccountBalance();
            assertNotNull (balance);
            log.info("Current Balance " + balance);

            WalletRequestDto dto = new WalletRequestDto();
            dto.setAmount(new BigDecimal(amount));
            accountService.chargeAccount(dto);

            var newBalance = accountService.findAccountBalance();
            assertNotNull(newBalance);

            //assertEquals(balance.getBalance().subtract(new BigDecimal(amount)), newBalance.getBalance());
        }
    }
}
