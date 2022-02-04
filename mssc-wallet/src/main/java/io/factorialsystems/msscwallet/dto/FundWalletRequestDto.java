package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundWalletRequestDto {
    @NotNull(message = "Amount to be funded must be specified")
    private BigDecimal amount;

    private String redirectUrl;
}
