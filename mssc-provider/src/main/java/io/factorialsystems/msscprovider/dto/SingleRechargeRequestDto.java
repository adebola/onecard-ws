package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleRechargeRequestDto {
    @NotNull(message = "Code must be specified")
    private String serviceCode;

    @NotNull(message = "Recipient must be specified")
    private String recipient;

    private String productId;
    private String telephone;
    private BigDecimal serviceCost;
    private String redirectUrl;
    private String paymentMode;
    private String accountType;
}
