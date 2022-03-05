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
public class SingleRechargeRequestDto {
    @NotEmpty(message = "Code must be specified")
    private String serviceCode;

    @NotEmpty(message = "Recipient must be specified")
    private String recipient;

    private String productId;
    private String telephone;

    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    private String redirectUrl;
    private String paymentMode;
    private String accountType;
}
