package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {
    private String id;
    private String userId;
    private String message;
    private Integer status;
    private String paymentId;
    private String rechargeId;
    private BigDecimal amount;
}
