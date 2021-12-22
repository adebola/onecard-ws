package io.factorialsystems.msscwallet.web.mapper;

import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.web.model.AccountDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(AccountMapstructMapperDecorator.class)
public interface AccountMapstructMapper {

    @Mappings({
            @Mapping(target = "accountType", ignore = true),
    })
    AccountDto accountToAccountDto(Account account);

    @Mappings({
            @Mapping(target = "accountType", ignore = true),
    })
    Account accountDtoToAccount(AccountDto accountDto);
    List<AccountDto> listAccountToAccountDto(List<Account> accounts);
    List<Account> listAccountDtoToAccount(List<AccountDto> accountDtos);
}
