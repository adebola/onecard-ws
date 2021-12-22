package io.factorialsystems.msscvoucher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cluster {
    private String id;
    private String name;
    private BigDecimal amount;
    private BigDecimal balance;
    private Boolean activated;
    private Timestamp activationDate;
    private String activatedBy;
    private String description;
    private String createdBy;
    private Timestamp createdDate;
    private Boolean suspended;
}
