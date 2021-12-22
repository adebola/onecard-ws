package io.factorialsystems.msscprovider.recharge.ringo;

import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;

public class RingoElectricRecharge implements Recharge, ParameterCheck {

    @Override
    public RechargeStatus recharge(RechargeRequest request) {
        return null;
    }

    @Override
    public Boolean check(RechargeRequest request) {
        return null;
    }
}
