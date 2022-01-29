package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    Page<Transaction> findUserTransactions(String id);
    Transaction findTransaction(String id);
    void save(Transaction transaction);
}
