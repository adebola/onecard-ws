package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.dto.AccountBalanceDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@CommonsLog
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountMapperTest {

    @Autowired
    AccountMapper accountMapper;

    @Test
    void findUserBalances() {
        final List<AccountBalanceDto> userBalances = accountMapper.findUserBalances();
        assertFalse(userBalances.isEmpty());
        log.info(userBalances.size());
    }

    @Test
    void findBalances() {
        List<String> ids = List.of("da015177-ec02-4cc1-af2a-7b61337f08d8", "4f712fa5-38e8-4567-9d87-033a0a458c53", "ac56ec4e-93ee-48c5-a71f-5bfc3045c63e");
        final List<AccountBalanceDto> balances = accountMapper.findBalances(ids);
        assertFalse(balances.isEmpty());
        assertEquals(balances.size(), 3);
        log.info(balances.size());
    }

    @Test
    void findBalances_Empty() {
        List<String> ids = new ArrayList<>();
        final List<AccountBalanceDto> balances = accountMapper.findBalances(ids);
        assertFalse(balances.isEmpty());
        log.info(balances.size());
    }

    @Test
    void findBalances_Null() {
        final List<AccountBalanceDto> balances = accountMapper.findBalances(null);
        assertFalse(balances.isEmpty());
        log.info(balances.size());
    }

    @Test
    void findActiveAccountByUserIdForUpdate_InvalidUser() {
        final Account account = accountMapper.findActiveAccountByUserIdForUpdate("InvalidUser");
        assertNull(account);
    }

    @Test
    void findActiveAccountByUserIdForUpdate_NoChargeAccountUser() {
        final String userId = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final Account account = accountMapper.findActiveAccountByUserIdForUpdate(userId);
        assertNotNull(account);

        assertThat(account.getName()).isEqualTo("foluke");
        log.info(account);

        final BigDecimal balance = account.getBalance();
        final BigDecimal add = balance.add(BigDecimal.valueOf(1000));
        account.setBalance(add);
        accountMapper.changeBalance(account);

        final Account accountByUserId = accountMapper.findAccountByUserId(userId);

        log.info(accountByUserId);
        assertThat(accountByUserId.getBalance()).isEqualTo(add);
    }

    @Test
    void findActiveAccountByUserIdForUpdate_ChargeAccountUser() {
        final String id = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
        final String userId = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        final Account account = accountMapper.findActiveAccountByUserIdForUpdate(userId);
        assertNotNull(account);

        assertThat(account.getId()).isEqualTo(id);
        assertThat(account.getName()).isEqualTo("foluke");
        log.info(account);
    }

    @Test
    void lockAccount() {
        final String accountId = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
        final Account account = accountMapper.findAccountByIdForUpdate(accountId);
        assertNotNull(account);
        log.info(account);
    }


}