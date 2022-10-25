package io.factorialsystems.msscprovider.recharge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechargeParameters {
    private Recharge recharge;
    private String serviceAction;
    private Integer rechargeProviderId;
    private String rechargeProviderCode;
}
