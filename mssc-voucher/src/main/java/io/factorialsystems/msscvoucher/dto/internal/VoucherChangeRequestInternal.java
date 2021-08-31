package io.factorialsystems.msscvoucher.dto.internal;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherChangeRequestInternal {
    private Date expiryDate;
    private Double denomination;
}
