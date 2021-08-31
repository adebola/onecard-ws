package io.factorialsystems.msscvoucher.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    private Integer id;
    private String code;
    private BigDecimal denomination;
    private String batchId;
    private Date expiryDate;
}
