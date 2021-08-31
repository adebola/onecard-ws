package io.factorialsystems.msscvoucher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Batch {
    private String id;
    private Date createdAt;
    private String createdBy;
    private BigDecimal denomination;
    private Integer count;
    private Boolean activated;
    private Date activationDate;
    private Date expiryDate;
}

