package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequestDto {
    private BigDecimal amount;
    private Integer serviceId;
//    private String serviceName;
//    private String requestId;
}
