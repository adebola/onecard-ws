package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.SingleRechargeRequest;

public interface ParameterCheck {
    Boolean check(SingleRechargeRequest request);
}
