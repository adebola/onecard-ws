package io.factorialsystems.msscvoucher.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherGenerationRequest {

    @NotNull
    Integer count;

    @NotNull
    BigDecimal denomination;
}
