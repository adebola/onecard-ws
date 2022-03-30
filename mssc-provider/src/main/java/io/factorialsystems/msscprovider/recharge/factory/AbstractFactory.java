package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ExtraDataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;

public abstract class AbstractFactory {
    public abstract Recharge getRecharge(String action);
    public abstract DataEnquiry getPlans(String action);
    public abstract ExtraDataEnquiry getExtraPlans(String action);
    public abstract ParameterCheck getCheck(String action);
}
