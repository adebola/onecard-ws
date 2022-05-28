package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutoRechargeResponseDto {
    private String id;
    private String message;
    private String paymentMode;
}
