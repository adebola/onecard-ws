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
public class SingleRechargeResponseDto {
    private String id;
    private Integer status;
    private String message;
    private BigDecimal amount;
    private String paymentMode;
    private String redirectUrl;
    private String authorizationUrl;
}
