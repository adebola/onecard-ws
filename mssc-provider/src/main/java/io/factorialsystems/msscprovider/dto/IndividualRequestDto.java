package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRequestDto {
    @NotEmpty
    private String serviceCode;

    private String productId;

    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    private String telephone;
    private String recipient;
}
