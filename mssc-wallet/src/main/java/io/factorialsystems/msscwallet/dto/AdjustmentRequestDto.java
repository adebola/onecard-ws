package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Builder
public class AdjustmentRequestDto {
    @NotBlank
    private String accountId;

    private BigDecimal amount;
    private String narrative;
}
