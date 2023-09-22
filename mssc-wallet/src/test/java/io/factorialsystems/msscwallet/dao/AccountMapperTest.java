package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.dto.AccountBalanceDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

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
        log.info(userBalances);
    }
}