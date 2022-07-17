package io.factorialsystems.msscprovider.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResolveRechargeDto {
    @NotBlank(message = "Recharge Id must be specified")
    private String id;

    @NotBlank(message= "Recharge Resolution must have a narrative")
    private String narrative;
}
