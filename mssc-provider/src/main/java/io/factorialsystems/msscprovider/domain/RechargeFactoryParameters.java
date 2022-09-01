package io.factorialsystems.msscprovider.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeFactoryParameters {
    private String providerCode;
    private String rechargeProviderCode;
    private String serviceAction;
    private Boolean async;
    private Boolean hasResults;
}
