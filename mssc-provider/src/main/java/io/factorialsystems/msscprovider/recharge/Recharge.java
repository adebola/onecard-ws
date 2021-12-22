package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.RechargeRequest;

public interface Recharge {
    RechargeStatus recharge(RechargeRequest request);
}
