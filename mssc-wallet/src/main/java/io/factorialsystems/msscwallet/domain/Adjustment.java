package io.factorialsystems.msscwallet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adjustment {
    private String id;
    private BigDecimal adjustedValue;
    private BigDecimal previousValue;
    private Timestamp adjustedOn;
    private String adjustedBy;
    private String accountId;
    private String narrative;
}