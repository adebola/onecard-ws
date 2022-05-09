package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {
    Page<Account> findAccounts();
    Account findAccountById(String id);
    Account findAccountByUserId(String id);
    Account findAccountByCorporateId(String id);
    Account findAccountByProviderId(String id);
    Account findAnonymousAccount();
    List<Account> findLowThresholdAccounts();
    void save(Account account);
    void update(Account account);
    void deleteAccount(Map<String, String> params);
    void addOrganizationWallet(Map<String, String> params);
    void removeOrganizationWallet(String id);
}

