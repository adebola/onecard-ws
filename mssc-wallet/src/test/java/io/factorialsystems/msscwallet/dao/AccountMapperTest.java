package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.dto.AccountBalanceDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.ArrayList;
import java.util.List;

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
}