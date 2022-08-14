package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class NewBalanceDto {
    private String id;
    private Integer status;
    private String errMessage;
    private BigDecimal balance;
}
