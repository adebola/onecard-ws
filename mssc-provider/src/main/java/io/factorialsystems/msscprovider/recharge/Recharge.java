package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;

public interface Recharge {
    RechargeStatus recharge(SingleRechargeRequest request);
}
