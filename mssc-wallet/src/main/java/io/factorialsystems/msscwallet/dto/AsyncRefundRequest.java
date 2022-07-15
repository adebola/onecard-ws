package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundRequest {
    private String userId;
    private BigDecimal amount;
    private String paymentId;
    private String singleRechargeId;
}
