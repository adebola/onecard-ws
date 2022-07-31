package io.factorialsystems.msscprovider.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
public class ResolveRechargeDto {
    @Null(message = "id cannot be set")
    private String id;

    @NotBlank(message = "RechargeId must be provided")
    private String rechargeId;

    @NotBlank(message = "Recharge Resolution must have a narrative")
    private String message;

   @Null(message = "resolvedBy cannot be set")
   private String resolvedBy;

   private Integer individualId;
}
