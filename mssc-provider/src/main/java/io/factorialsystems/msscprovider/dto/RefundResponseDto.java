package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RefundResponseDto {
    private String id;
    private String userId;
    private String message;
    private Integer status;
    private String paymentId;
    private String rechargeId;
    private BigDecimal amount;
}
