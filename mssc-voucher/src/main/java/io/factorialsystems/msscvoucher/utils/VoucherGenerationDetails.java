package io.factorialsystems.msscvoucher.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
public class VoucherGenerationDetails {
    private String batchId;
    private String hashedCode;
    private BigDecimal denomination;
    private Date expiryDate;
}
