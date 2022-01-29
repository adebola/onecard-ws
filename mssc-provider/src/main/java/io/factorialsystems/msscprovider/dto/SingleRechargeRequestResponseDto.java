package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleRechargeRequestResponseDto {
    private String id;
    private Integer status;
    private String message;
    private String paymentMode;
    private String redirectUrl;
    private String authorizationUrl;
}
