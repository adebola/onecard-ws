package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleRechargeRequestDto {
    @Null(message = "Id cannot be set")
    private String id;

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

    private String name;

    @Null(message = "Created Date cannot be set")
    private Date createdAt;
}
