package io.factorialsystems.msscwallet.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Test
    void findUserTransactions() {
    }

    @Test
    void findOrganizationTransactionsByAccountId() {
        final String id = "13de0dc8-68e7-48fe-bb9b-e78a46fd540e";
        var x = transactionService.findOrganizationTransactionsByAccountId(id, 1 , 20);
        log.info(x.getList().size());
    }


    @Test
    void findTransaction() {
    }
}