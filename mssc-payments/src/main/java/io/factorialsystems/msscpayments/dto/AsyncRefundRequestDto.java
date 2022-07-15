package io.factorialsystems.msscpayments.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundRequestDto {
    private String userId;
    private String paymentId;
    private BigDecimal amount;
    private String singleRechargeId;
}
