package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequestDto {

    @Null(message = "Id cannot be set")
    private Integer id;

    @NotNull(message = "Code must be specified")
    private String serviceCode;

    @NotNull(message = "Recipient must be specified")
    private String recipient;

    private String productId;

    private String telephone;

    private BigDecimal serviceCost;

    private String redirectUrl;

    @Null(message = "Authorization URL cannot be set")
    private String authorizationUrl;
}
