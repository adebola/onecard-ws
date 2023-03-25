package io.factorialsystems.msscapiuser.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeRequestDto {

    @ApiModelProperty(hidden = true)
    @Null(message = "id must be null")
    private String id;

    @ApiModelProperty(value = "Payment Type", name =  "paymentMode", dataType = "String", example = "wallet or paystack")
    private String paymentMode;

    @ApiModelProperty(hidden = true)
    private String redirectUrl;

    @ApiModelProperty(hidden = true)
    @Null(message = "total service cost cannot be set it is computed")
    private BigDecimal totalServiceCost;

    @ApiModelProperty(hidden = true)
    @Null(message = "date cannot be set")
    private Date createdAt;

    @ApiModelProperty(hidden = true)
    @Null(message = "AutoRequestId cannot be set")
    private String autoRequestId;

    @NotEmpty
    private List<@Valid IndividualRequestDto> recipients;
}
