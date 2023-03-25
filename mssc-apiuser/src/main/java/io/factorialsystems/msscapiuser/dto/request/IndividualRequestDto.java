package io.factorialsystems.msscapiuser.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRequestDto {

    @ApiModelProperty(hidden = true)
    @Null(message = "id cannot be set")
    private Integer id;

    @NotEmpty
    @ApiModelProperty(value = "ServiceCode", name =  "serviceCode", dataType = "string", example = "GLO-AIRTIME")
    private String serviceCode;

    @ApiModelProperty(value = "ProductId", name =  "productId", dataType = "string", example = "productId for data plan")
    private String productId;

    @ApiModelProperty(value = "ServiceCost", name =  "serviceCost", dataType = "number", example = "1000, must not be specified for a data plan")
    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    @ApiModelProperty(value = "telephone no", name =  "telephone", dataType = "string", example = "08055572307 optional for electricity recharges")
    private String telephone;

    @NotEmpty
    @ApiModelProperty(value = "recipient", name =  "recipient", dataType = "string", example = "08055572307")
    private String recipient;

    @ApiModelProperty(hidden = true)
    @Null(message = "Failed cannot be set")
    private Boolean failed;

    @ApiModelProperty(hidden = true)
    @Null(message = "Failure message cannot be set")
    private String failedMessage;

    @ApiModelProperty(hidden = true)
    @Null(message = "RefundId cannot be set")
    private String refundId;

    @ApiModelProperty(hidden = true)
    @Null(message = "Successful Retry Id cannot be set")
    private String retryId;

    @ApiModelProperty(hidden = true)
    @Null(message = "ResolveId cannot be set")
    private String resolveId;

    @ApiModelProperty(hidden = true)
    @Null(message = "Results cannot be set")
    private String results;
}
