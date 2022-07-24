package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundRequestDto {
    private String userId;
    private BigDecimal amount;
    private String paymentId;
    private String bulkRechargeId;
    private String singleRechargeId;
    private Integer individualRechargeId;
}
