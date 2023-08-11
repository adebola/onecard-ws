package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.AccountLedgerEntry;
import io.factorialsystems.msscwallet.domain.query.AccountLedgerSearch;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountLedgerMapper {
    Page<AccountLedgerEntry> findAll();
    AccountLedgerEntry findById(String id);
    List<AccountLedgerEntry> findByAccountId(String id);
    BigDecimal findTotalExpenditureByDay(AccountLedgerSearch ledgerSearch);
    int save(AccountLedgerEntry accountLedgerEntry);
}
