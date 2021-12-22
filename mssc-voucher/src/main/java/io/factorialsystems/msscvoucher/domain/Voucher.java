package io.factorialsystems.msscvoucher.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    private Integer id;
    private String code;
    private String serialNumber;
    private String batchId;
    private BigDecimal denomination;
    private Timestamp expiryDate;
    private Timestamp createdAt;
    private String createdBy;
    private Boolean activated;
    private Boolean suspended;
    private Timestamp activationDate;
    private String activatedBy;
}
