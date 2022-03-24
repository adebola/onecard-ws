package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoAirtimeRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.RingoElectricRecharge;
import io.factorialsystems.msscprovider.recharge.ringo.*;
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
        codeMapper.put("SPECTRANET-DATA", "SPECTRANET");
        codeMapper.put("SMILE-DATA", "SMILE");
    }

    @Override
    public Recharge getRecharge(String action) {
        return getClass(action);
    }

    @Override
    public DataEnquiry getPlans(String action) {

        if (action.equalsIgnoreCase("DATA") || action.equalsIgnoreCase("GLO-DATA") ||
                action.equalsIgnoreCase("MTN-DATA") || action.equalsIgnoreCase("AIRTEL-DATA") ||
                action.equalsIgnoreCase("9MOBILE-DATA")) {
            return ApplicationContextProvider.getBean(RingoMobileDataRecharge.class);
        } else if (action.equalsIgnoreCase("SPECTRANET") || action.equalsIgnoreCase("SPECTRANET-DATA")) {
            return ApplicationContextProvider.getBean(RingoSpectranetRecharge.class);
        } else if (action.equalsIgnoreCase("SMILE") || action.equalsIgnoreCase("SMILE-DATA")) {
            return ApplicationContextProvider.getBean(RingoSmileRecharge.class);
        }

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
        if (action.equalsIgnoreCase("AIRTIME")) {
            return ApplicationContextProvider.getBean(RingoAirtimeRecharge.class);
        } else if (action.equalsIgnoreCase("DATA")) {
            return ApplicationContextProvider.getBean(RingoMobileDataRecharge.class);
        } else if (action.equalsIgnoreCase("ELECTRICITY")) {
            return ApplicationContextProvider.getBean(RingoElectricRecharge.class);
        } else if (action.equalsIgnoreCase("SPECTRANET")) {
            return ApplicationContextProvider.getBean(RingoSpectranetRecharge.class);
        } else if  (action.equalsIgnoreCase("SMILE")) {
            return ApplicationContextProvider.getBean(RingoSmileRecharge.class);
        }else if (action.equalsIgnoreCase("DSTV")) {
            return ApplicationContextProvider.getBean(RingoDstvRecharge.class);
        }

        return null;
    }
}
