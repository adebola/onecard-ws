package io.factorialsystems.msscprovider.recharge.factory.telco;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.mtn.MtnAirtimeRecharge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MtnRechargeFactory extends AbstractFactory {

    public static final Map<String, String> codeMapper = new HashMap<>();

    static {
        codeMapper.put("MTN-AIRTIME", "MFIN-5-OR");
        codeMapper.put("MTN-DATA", "mtn");
    }

    @Override
    public Recharge getRecharge(String action) {
        return getClass(action);
    }

    @Override
    public DataEnquiry getPlans(String action) {
//        return ApplicationContextProvider.getBean(MTNDataRecharge.class);
        return null;
    }

    @Override
    public ParameterCheck getCheck(String action) {

        Recharge recharge = getClass(action);

        if (!(recharge instanceof  ParameterCheck)) {
            throw new RuntimeException("ParameterCheck Interface not implemented");
        }

        return (ParameterCheck) recharge;
    }

    private Recharge getClass(String action) {
        if (action.equalsIgnoreCase("AIRTIME"))
            return ApplicationContextProvider.getBean(MtnAirtimeRecharge.class);

        return null;
    }
}
