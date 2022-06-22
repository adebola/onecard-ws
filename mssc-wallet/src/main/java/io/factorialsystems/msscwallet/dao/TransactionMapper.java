package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.domain.query.SearchByDateRange;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
    Page<Transaction> findUserTransactions(String id);
    Page<Transaction> findOrganizationTransactionsByAccountId(String id);
    Transaction findTransaction(String id);
    void save(Transaction transaction);
    List<Transaction> findUserTransactionByDateRange(SearchByDateRange range);
}
