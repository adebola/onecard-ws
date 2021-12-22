package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.ekedp.EKEDPElectricRecharge;

public class EKEDPRechargeFactory extends AbstractFactory {

    @Override
    public Recharge getRecharge(String action) {
        if (action.equalsIgnoreCase("ELECTRICITY")) {
            return ApplicationContextProvider.getBean(EKEDPElectricRecharge.class);
        }

        return null;
    }

    @Override
    public DataEnquiry getPlans(String code) {
        return null;
    }

    @Override
    public ParameterCheck getCheck(String s) {
        return null;
    }
}
