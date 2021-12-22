package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    Page<Account> findAccounts();
    Account findAccountById(String id);
    Account findAccountByUserId(String id);
    Account findAccountByCorporateId(String id);
    Account findAccountByProviderId(String id);
    List<Account> findLowThresholdAccounts();
    void save(Account account);
    void update(Account account);


}
