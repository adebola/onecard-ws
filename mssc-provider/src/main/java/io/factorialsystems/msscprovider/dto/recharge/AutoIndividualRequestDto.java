package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoIndividualRequestDto {

    @Null(message = "Id cannot be set")
    private Integer id;

    @NotEmpty(message = "Please provide ServiceCde")
    private String serviceCode;

    @Min(0L)
    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    private String productId;

    private String telephone;

    @NotEmpty(message = "Please provide Recipient")
    private String recipient;
}

