package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundWalletResponseDto {
    private String id;
    private String authorizationUrl;
    private String redirectUrl;
}
