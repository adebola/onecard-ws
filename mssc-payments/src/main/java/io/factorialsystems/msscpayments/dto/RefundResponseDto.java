package io.factorialsystems.msscpayments.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponseDto {
    private String id;
    private Integer status;
    private String  paymentId;
    private String message;
}
