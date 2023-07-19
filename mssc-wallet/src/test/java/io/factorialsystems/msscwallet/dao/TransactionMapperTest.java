package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.dto.TransactionSearchRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionMapperTest {

    @Autowired
    TransactionMapper transactionMapper;

    @Test
    void findUserTransactions() {
    }

    @Test
    void findOrganizationTransactionsByAccountId() {
    }

    @Test
    void findTransaction() {
    }

    @Test
    void save() {
    }

    @Test
    void findUserTransactionByDateRange() {
    }

    @Test
    void search() {
        TransactionSearchRequestDto dto = new TransactionSearchRequestDto();
        //final String id = "0842f6d1-3e68-421a-b7b5-19ab296f6776";
        //dto.setUserId(id);
        final List<Transaction> search = transactionMapper.search(dto);
        log.info("Size {}", search.size());

    }
}