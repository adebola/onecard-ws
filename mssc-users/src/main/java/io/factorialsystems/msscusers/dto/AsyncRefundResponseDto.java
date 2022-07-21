package io.factorialsystems.msscusers.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AsyncRefundResponseDto {
    private String id;
    private String userId;
    private String message;
    private Integer status;
    private String paymentId;
    private String rechargeId;
    private BigDecimal amount;
    private String bulkRechargeId;
    private Integer individualRechargeId;
}
