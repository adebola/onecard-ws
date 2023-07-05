package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AdjustmentRequestDto {
    @NotEmpty
    private String accountId;

    @NotNull
    private BigDecimal amount;

    @NotEmpty
    private String narrative;
}
