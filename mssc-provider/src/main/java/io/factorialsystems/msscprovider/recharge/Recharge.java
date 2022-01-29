package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.SingleRechargeRequest;

public interface Recharge {
    RechargeStatus recharge(SingleRechargeRequest request);
}
