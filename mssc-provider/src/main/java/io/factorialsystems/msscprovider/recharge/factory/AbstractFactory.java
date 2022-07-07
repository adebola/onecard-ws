package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.*;

public abstract class AbstractFactory {
    public abstract Recharge getRecharge(String action);
    public abstract DataEnquiry getPlans(String action);
    public abstract ExtraDataEnquiry getExtraPlans(String action);
    public abstract ParameterCheck getCheck(String action);
    public abstract Balance getBalance();
    public abstract ReQuery getReQuery();
}
