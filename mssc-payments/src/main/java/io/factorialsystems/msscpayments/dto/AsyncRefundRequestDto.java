package io.factorialsystems.msscpayments.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Builder
public class AsyncRefundRequestDto {
    private String userId;
    private String paymentId;

    @Min(1L)
    private BigDecimal amount;
    private String bulkRechargeId;
    private String singleRechargeId;
    private Integer individualRechargeId;
}
