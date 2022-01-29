package io.factorialsystems.msscwallet.mapper;

import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class AccountMapstructMapperDecorator implements AccountMapstructMapper {

    private AccountMapstructMapper mapper;
    private static final List<String> accountTypes = Arrays.asList("ACCOUNT_USER", "ACCOUNT_PROVIDER");

    @Autowired
    public void setMapstructMapper(AccountMapstructMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public AccountDto accountToAccountDto(Account account) {

        String acctType = accountTypes.get(account.getAccountType() - 1);

        if (acctType != null) {
            AccountDto dto = mapper.accountToAccountDto(account);
            dto.setAccountType(acctType);
            return dto;
        }

        throw new RuntimeException(String.format("Error Converting Object from Account to AccountDto Cannot find corresponding AccountType: %d", account.getAccountType()));
    }

    @Override
    public Account accountDtoToAccount(AccountDto accountDto) {

        if (accountTypes.contains(accountDto.getAccountType())) {
            Account account = mapper.accountDtoToAccount(accountDto);
            account.setAccountType(accountTypes.indexOf(accountDto.getAccountType()));

            return account;
        }

        log.error(String.format("Error Converting Object from AccountDto to Account Cannot find corresponding Account Type for %s", accountDto.getAccountType()));
        return null;
    }

    @Override
    public List<AccountDto> listAccountToAccountDto(List<Account> accounts) {
        return mapper.listAccountToAccountDto(accounts);
    }

    @Override
    public List<Account> listAccountDtoToAccount(List<AccountDto> accountDtos) {
        return mapper.listAccountDtoToAccount(accountDtos);
    }
}
