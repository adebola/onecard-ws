package io.factorialsystems.msscprovider.domain.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProviderTotal {
    private String provider;
    private BigDecimal total;
}
