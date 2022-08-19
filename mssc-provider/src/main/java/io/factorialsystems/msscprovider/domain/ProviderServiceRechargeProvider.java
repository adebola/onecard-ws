package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderServiceRechargeProvider {

    @NotNull(message = "recharge Id must be specified")
    private Integer rechargeId;

    @NotNull(message = "service Id must be specified")
    private Integer serviceId;

    @NotNull(message = "Priority must be specified")
    private Integer priority;
}
