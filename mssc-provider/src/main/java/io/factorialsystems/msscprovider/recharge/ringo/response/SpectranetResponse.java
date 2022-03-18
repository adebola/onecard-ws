package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpectranetResponse {
    private BigDecimal amount;
    private BigDecimal amountCharged;
    private String message;
    private Boolean pin_based;
    private Integer status;
    private String TransRef;

    private List<SpectranetPIN> pin;
}
