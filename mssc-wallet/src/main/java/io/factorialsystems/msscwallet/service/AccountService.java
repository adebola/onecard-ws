package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.mapper.AccountMapstructMapper;
import io.factorialsystems.msscwallet.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public BalanceDto findAccountBalance() {
        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User must be Logged In to retrieve Account Balance");
        }

        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException(String.format("Unable Load Account for (%s), Account not found please contact support", userId));
        }

        return BalanceDto.builder()
                .balance(account.getBalance())
                .build();
    }

    public AccountDto findAccountById(String id) {
        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountById(id));
    }

    public AccountDto findAccountByUserId(String userId) {
        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByUserId(userId));
    }

    public AccountDto findAccountByCorporateId(String userId) {
        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByCorporateId(userId));
    }

    public AccountDto findAccountByProviderId(String providerId) {
        return  accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByProviderId(providerId));
    }


    public void updateAccount(String id, AccountDto accountDto) {

        if (id != null && accountDto != null) {
            accountDto.setId(id);
            accountMapper.update(accountMapstructMapper.accountDtoToAccount(accountDto));

            String auditMessage = String.format("Account %s updated", accountDto.getId());
            auditService.auditEvent(auditMessage, ACCOUNT_UPDATED);
        }
    }

    public void updateAccountBalance(String id, BalanceDto dto) {

        Account account = accountMapper.findAccountByUserId(id);

        if (account == null) {
            throw new RuntimeException(String.format("Unable Load Account for (%s), Account not found please contact support", id));
        }

        account.setBalance(dto.getBalance());
        accountMapper.update(account);

        String auditMessage = String.format("Account (%s / %s) Balance updated updated to %.2f by (%s)",account.getId(), account.getName(), account.getBalance().doubleValue(), K.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_UPDATED);
    }

    public WalletResponseDto chargeAccount(WalletRequestDto dto) {
        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User must be logged to charge Wallet");
        }

        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException(String.format("Unable to retrieve Account for (%s) for the Database, please contact support", userId));
        }

        if (account.getBalance().compareTo(dto.getAmount()) >= 0)  {
            BigDecimal newValue = account.getBalance().subtract(dto.getAmount());
            account.setBalance(newValue);
            accountMapper.update(account);


            return WalletResponseDto.builder()
                    .message("Successful")
                    .status(200)
                    .build();
        }

        return WalletResponseDto.builder()
                .message("Insufficient Balance")
                .status(300)
                .build();
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
