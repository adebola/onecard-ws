package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.RechargeRequest;

public interface ParameterCheck {
    Boolean check(RechargeRequest request);
}
