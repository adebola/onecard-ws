package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdjustmentResponseDto {
    private String id;
    private Integer status;
    private String message;
    private BigDecimal balance;
}
