package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRequestDto {
    @NotNull
    private String serviceCode;

    private String productId;
    private BigDecimal serviceCost;
    private String telephone;
    private String recipient;
}
