package io.factorialsystems.msscwallet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class AccountBalanceDto {
    private String userId;
    private String accountId;
    private BigDecimal balance;
}
