package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferFundsDto {

    @NotBlank(message = "A recipient must be specified")
    private String recipient;

    @NotNull(message = "Transfer Amount must be specified")
    @Min(value=0)
    private BigDecimal amount;
}
