package io.factorialsystems.msscpayments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {
    private String id;
    private Integer status;
    private String paymentId;
    private String message;
}
