package io.factorialsystems.msscprovider.dto.recharge;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundRequestDto {
    private String userId;
    private BigDecimal amount;
    private String paymentId;
    private String singleRechargeId;
    private String bulkRechargeId;
    private Integer individualRechargeId;
}
