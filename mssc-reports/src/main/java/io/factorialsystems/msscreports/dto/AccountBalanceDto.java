package io.factorialsystems.msscreports.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class AccountBalanceDto {
    private String userId;
    private String accountId;
    private BigDecimal balance;
}
