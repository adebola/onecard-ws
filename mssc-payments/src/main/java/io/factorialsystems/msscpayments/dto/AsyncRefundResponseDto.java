package io.factorialsystems.msscpayments.dto;

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
}
