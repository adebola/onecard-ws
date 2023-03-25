package io.factorialsystems.msscapiuser.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraPlanRequestDto {
    @NotEmpty(message = "Recipient must be specified")
    @ApiModelProperty(value = "recipient", name =  "recipient", dataType = "string", example = "08055572307")
    private String recipient;

    @NotEmpty(message = "Service Code must be specified")
    @ApiModelProperty(value = "ServiceCode", name =  "serviceCode", dataType = "string", example = "GLO-AIRTIME")
    private String serviceCode;

    @ApiModelProperty(value = "accountType", name =  "accountType", dataType = "string", example = "prepaid")
    private String accountType;
}
