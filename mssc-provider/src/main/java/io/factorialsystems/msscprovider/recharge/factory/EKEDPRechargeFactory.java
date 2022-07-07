package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.ekedp.EKEDPElectricRecharge;

import java.util.HashMap;
import java.util.Map;

public class EKEDPRechargeFactory extends AbstractFactory {

    public static final Map<String, String> codeMapper = new HashMap<>();
    public static final String ACCOUNT_TYPE_PREPAID = "prepaid";
    public static final String ACCOUNT_TYPE_POSTPAID = "postpaid";

    static {
        codeMapper.put("prepaid", "OFFLINE_PREPAID");
        codeMapper.put("postpaid", "OFFLINE_POSTPAID");
    }

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
    public ExtraDataEnquiry getExtraPlans(String action) {
        return null;
    }

    @Override
    public ParameterCheck getCheck(String s) {
        return ApplicationContextProvider.getBean(EKEDPElectricRecharge.class);
    }

    @Override
    public Balance getBalance() {
        return null;
    }

    @Override
    public ReQuery getReQuery() {
        return null;
    }
}
