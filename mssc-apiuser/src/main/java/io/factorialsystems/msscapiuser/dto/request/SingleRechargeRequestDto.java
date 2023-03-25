package io.factorialsystems.msscapiuser.dto.request;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "ServiceCode", name =  "serviceCode", dataType = "string", example = "GLO-AIRTIME")
    @NotEmpty(message = "Code must be specified")
    private String serviceCode;
    @NotEmpty(message = "Recipient must be specified")
    @ApiModelProperty(value = "recipient", name =  "recipient", dataType = "string", example = "08055572307")
    private String recipient;
    @ApiModelProperty(value = "ProductId", name =  "productId", dataType = "string", example = "productId for data plan")
    private String productId;
    @ApiModelProperty(value = "telephone no", name =  "telephone", dataType = "string", example = "08055572307 optional for electricity recharges")
    private String telephone;
    @Digits(integer = 9, fraction = 2)
    @ApiModelProperty(value = "ServiceCost", name =  "serviceCost", dataType = "number", example = "1000, must not be specified for a data plan")
    private BigDecimal serviceCost;
    @ApiModelProperty(hidden = true)
    private String redirectUrl;
    private String paymentMode;
    @ApiModelProperty(hidden = true)
    private String accountType;
    @ApiModelProperty(hidden = true)
    private String name;
}

