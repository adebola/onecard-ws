package io.factorialsystems.msscprovider.recharge.factory.telco;

import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.Recharge;

public class MtnRechargeFactory implements TelcoAbstractFactory {

    @Override
    public Recharge getRecharge(RechargeAction rechargeAction) {
        return null;
    }

    @Override
    public DataEnquiry getPlans(DataPlanAction dataPlanAction) {
        return null;
    }
}
