package io.factorialsystems.msscwallet.domain;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adjustment {
    private String id;
    private String fundWalletRequestId;
    private BigDecimal adjustedValue;
    private BigDecimal previousValue;
    private Timestamp adjustedOn;
    private String adjustedBy;
    private String accountId;
    private String narrative;
}