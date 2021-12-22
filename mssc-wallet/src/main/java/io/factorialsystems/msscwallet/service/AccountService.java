package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.utils.K;
import io.factorialsystems.msscwallet.web.mapper.AccountMapstructMapper;
import io.factorialsystems.msscwallet.web.model.AccountDto;
import io.factorialsystems.msscwallet.web.model.PagedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AuditService auditService;
    private final AccountMapper accountMapper;
    private final AccountMapstructMapper accountMapstructMapper;

    private static final String[] AccountType = {"USER", "CORPORATE", "PROVIDER"};
    private static final String ACCOUNT_CREATED = "Account Created";
    private static final String ACCOUNT_UPDATED = "Account Updated";
    private static final String ACCOUNT_BALANCE_UPDATED = "Account Balance Updated";

    public PagedDto<AccountDto> findAccounts(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Account> accounts = accountMapper.findAccounts();

        return createDto(accounts);
    }

    public AccountDto findAccountById(String id) {

        Account account = accountMapper.findAccountById(id);

        if (account != null) {
            return accountMapstructMapper.accountToAccountDto(account);
        }

        return null;
    }

    public AccountDto findAccountByUserId(String userId) {
        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            return accountMapstructMapper.accountToAccountDto(createUserAccount(userId));
        }

        return null;
    }

    public AccountDto findAccountByCorporateId(String userId) {
        Account account = accountMapper.findAccountByCorporateId(userId);

        if (account == null) {
            return accountMapstructMapper.accountToAccountDto(createCorporateAccount(userId));
        }

        return null;
    }

    public AccountDto findAccountByProviderId(String providerId) {
        Account account = accountMapper.findAccountByProviderId(providerId);

        if (account == null) {
            return accountMapstructMapper.accountToAccountDto(createProviderAccount(providerId));
        }

        return accountMapstructMapper.accountToAccountDto(account);
    }


    public void updateAccount(String id, AccountDto accountDto) {

        if (id != null && accountDto != null) {
            accountDto.setId(id);
            accountMapper.update(accountMapstructMapper.accountDtoToAccount(accountDto));

            String auditMessage = String.format("Account %s updated", accountDto.getId());
            auditService.auditEvent(auditMessage, ACCOUNT_UPDATED);
        }
    }

    public void updateAccountBalance(String id, AccountDto accountDto) {

        if (id != null && accountDto != null && accountDto.getBalance() != null) {

            Account account = Account.builder()
                    .id(id)
                    .balance(accountDto.getBalance())
                    .build();

            accountMapper.update(account);

            String auditMessage = String.format("Account %s Balance updated updated to %.2f", accountDto.getId(), accountDto.getBalance().doubleValue());
            auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_UPDATED);
        }
    }

    private Account createAccount(String id, Integer accountType) {

        Account account = Account.builder()
                .accountType(accountType)
                .userId(id)
                .createdBy(K.getUserName())
                .id(String.valueOf(UUID.randomUUID()))
                .build();

        accountMapper.save(account);

        String auditMessage = String.format("Account of Type %s with Id %s created", AccountType[accountType - 1], account.getId());
        auditService.auditEvent(auditMessage, ACCOUNT_CREATED);

        return accountMapper.findAccountById(account.getId());
    }

    private Account createUserAccount(String userId) {
        return createAccount(userId, 1);
    }
    private Account createCorporateAccount(String userId) { return createAccount(userId,2); }
    private Account createProviderAccount(String providerId) {
        return createAccount(providerId, 3);
    }

    private PagedDto<AccountDto> createDto(Page<Account> accounts) {
        PagedDto<AccountDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) accounts.getTotal());
        pagedDto.setPageNumber(accounts.getPageNum());
        pagedDto.setPageSize(accounts.getPageSize());
        pagedDto.setPages(accounts.getPages());
        pagedDto.setList(accountMapstructMapper.listAccountToAccountDto(accounts.getResult()));
        return pagedDto;
    }
}
