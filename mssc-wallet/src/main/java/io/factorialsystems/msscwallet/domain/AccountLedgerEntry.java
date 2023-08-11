package io.factorialsystems.msscwallet.domain;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountLedgerEntry {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private Timestamp createdOn;
    private Integer operation;
    private String description;
}
