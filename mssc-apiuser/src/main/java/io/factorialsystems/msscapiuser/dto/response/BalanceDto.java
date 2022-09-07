package io.factorialsystems.msscapiuser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    @NotNull(message = "Please specify the Balance")
    BigDecimal balance;

    @NotBlank
    private String narrative;
}
