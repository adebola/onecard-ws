package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundResponseDto {
    private String id;
    private String userId;
    private String message;
    private Integer status;
    private String paymentId;
    private String rechargeId;
    private BigDecimal amount;
}
