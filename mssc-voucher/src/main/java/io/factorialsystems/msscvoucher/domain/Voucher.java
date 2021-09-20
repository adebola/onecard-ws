package io.factorialsystems.msscvoucher.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    private Integer id;
    private String code;
    private String serialNumber;
    private String batchId;
    private BigDecimal denomination;
    private Date expiryDate;
    private Date createdAt;
    private String createdBy;
    private Boolean activated;
}
