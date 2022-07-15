package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponseDto {
    private String id;
    private String rechargeId;
    private Integer status;
    private String  paymentId;
    private String message;
}
