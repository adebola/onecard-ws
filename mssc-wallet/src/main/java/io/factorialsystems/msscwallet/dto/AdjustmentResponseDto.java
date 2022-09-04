package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustmentResponseDto {
    private String id;
    private Integer status;
    private String message;
}
