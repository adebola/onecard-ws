package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRechargeResponseDto {
    private String id;
    private Integer status;
    private String message;
    private String authorizationUrl;
    private String paymentMode;
    private String redirectUrl;
}
