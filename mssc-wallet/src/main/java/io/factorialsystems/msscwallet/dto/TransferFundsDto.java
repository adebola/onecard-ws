package io.factorialsystems.msscwallet.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferFundsDto {

    @NotBlank(message = "A recipient must be specified")
    private String recipient;

    @NotNull(message = "Transfer Amount must be specified")
    private BigDecimal amount;
}
