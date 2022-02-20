package io.factorialsystems.msscprovider.recharge.factory.telco;

import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.Recharge;

public interface TelcoAbstractFactory {
    Recharge getRecharge(RechargeAction rechargeAction);
    DataEnquiry getPlans(DataPlanAction dataPlanAction); //Not sure about this data-type yet.
}
