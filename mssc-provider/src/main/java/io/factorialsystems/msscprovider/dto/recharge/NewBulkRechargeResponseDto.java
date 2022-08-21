package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeResponseDto {
    private String id;
    private Integer status;
    private String message;
    private String paymentMode;
    private String redirectUrl;
    private BigDecimal totalCost;
    private String authorizationUrl;
}
