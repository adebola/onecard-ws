package io.factorialsystems.msscvoucher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    private String id;
    private String clusterId;
    private Timestamp createdDate;
    private String createdBy;
    private BigDecimal denomination;
    private Integer voucherCount;
    private Boolean activated;
    private Timestamp activationDate;
    private String activatedBy;
    private Timestamp expiryDate;
    private Boolean suspended;
}

