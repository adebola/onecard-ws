package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoAirtimeRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoDataRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoDstvRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoElectricRecharge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RingoRechargeFactory extends AbstractFactory {

    public static final Map<String, String> codeMapper = new HashMap<>();

    static {
        codeMapper.put("MTN-AIRTIME", "MFIN-5-OR");
        codeMapper.put("AIRTEL-AIRTIME", "MFIN-1-OR");
        codeMapper.put("GLO-AIRTIME", "MFIN-6-OR");
        codeMapper.put("9MOBILE-AIRTIME", "MFIN-2-OR");
        codeMapper.put("MTN-DATA", "mtn");
        codeMapper.put("AIRTEL-DATA", "airtel");
        codeMapper.put("GLO-DATA", "glo");
        codeMapper.put("9MOBILE-DATA", "9mobile");
        codeMapper.put("EKEDP", "EKEDC");
        codeMapper.put("JED", "JEDC");
        codeMapper.put("DSTV", "DSTV");
    }

    @Override
    public Recharge getRecharge(String action) {
        return getClass(action);
    }

    @Override
    public DataEnquiry getPlans(String action) {
        return ApplicationContextProvider.getBean(RingoDataRecharge.class);
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
        if (action.equalsIgnoreCase("AIRTIME")) {
            return ApplicationContextProvider.getBean(RingoAirtimeRecharge.class);
        } else if (action.equalsIgnoreCase("DATA")) {
            return ApplicationContextProvider.getBean(RingoDataRecharge.class);
        } else if (action.equalsIgnoreCase("ELECTRICITY")) {
            return ApplicationContextProvider.getBean(RingoElectricRecharge.class);
        }else if (action.equalsIgnoreCase("DSTV")) {
            return ApplicationContextProvider.getBean(RingoDstvRecharge.class);
        }

        return null;
    }
}
