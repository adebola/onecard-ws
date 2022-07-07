package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.*;
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
        codeMapper.put("GOTV", "GOTV");
        codeMapper.put("STARTIMES", "STARTIMES");
        codeMapper.put("SPECTRANET-DATA", "SPECTRANET");
        codeMapper.put("SMILE-DATA", "SMILE");
    }

    public static final String DATA_LABEL = "DATA";
    public static final String GLO_DATA_LABEL = "GLO-DATA";
    public static final String MTN_DATA_LABEL = "MTN-DATA";
    public static final String AIRTEL_DATA_LABEL = "AIRTEL-DATA";
    public static final String NINEMOBILE_DATA_LABEL = "9MOBILE-DATA";
    public static final String SPECTRANET_LABEL = "SPECTRANET";
    public static final String SPECTRANET_DATA_LABEL = "SPECTRANET-DATA";
    public static final String SMILE_LABEL = "SMILE";
    public static final String SMILE_DATA_LABEL = "SMILE-DATA";
    public static final String DSTV_LABEL = "DSTV";
    public static final String GOTV_LABEL = "GOTV";
    public static final String STARTIMES_LABEL = "STARTIMES";
    public static final String AIRTIME_LABEL = "AIRTIME";
    public static final String ELECTRICITY_LABEL = "ELECTRICITY";
    public static final String EKEDP_LABEL = "EKEDP";

    @Override
    public Recharge getRecharge(String action) {
        return getClass(action);
    }

    @Override
    public DataEnquiry getPlans(String action) {

        DataEnquiry enquiry = null;

        switch (action) {
            case DATA_LABEL:
            case GLO_DATA_LABEL:
            case MTN_DATA_LABEL:
            case AIRTEL_DATA_LABEL:
            case NINEMOBILE_DATA_LABEL:
                enquiry = ApplicationContextProvider.getBean(RingoMobileDataRecharge.class);
                break;

            case SPECTRANET_LABEL:
            case SPECTRANET_DATA_LABEL:
                enquiry =  ApplicationContextProvider.getBean(RingoSpectranetRecharge.class);
                break;

            case SMILE_LABEL:
            case SMILE_DATA_LABEL:
                enquiry =  ApplicationContextProvider.getBean(RingoSmileRecharge.class);
                break;
        }

        return enquiry;
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {
        ExtraDataEnquiry extraEnquiry = null;

        switch (action) {
            case DSTV_LABEL:
            case GOTV_LABEL:
            case STARTIMES_LABEL:
                extraEnquiry =  ApplicationContextProvider.getBean(RingoDstvRecharge.class);
                break;

            case EKEDP_LABEL:
                extraEnquiry = ApplicationContextProvider.getBean(RingoElectricRecharge.class);
        }

        return extraEnquiry;
    }

    @Override
    public ParameterCheck getCheck(String action) {
        Recharge recharge = getClass(action);

        if (!(recharge instanceof  ParameterCheck)) {
            throw new RuntimeException("ParameterCheck Interface not implemented");
        }

        return (ParameterCheck) recharge;
    }

    @Override
    public Balance getBalance() {
        return ApplicationContextProvider.getBean(RingoAirtimeRecharge.class);
    }

    @Override
    public ReQuery getReQuery() {
        return ApplicationContextProvider.getBean(RingoAirtimeRecharge.class);
    }

    private Recharge getClass(String action) {
        Recharge recharge = null;

        switch (action) {
            case AIRTIME_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoAirtimeRecharge.class);
                break;

            case DATA_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoMobileDataRecharge.class);
                break;

            case ELECTRICITY_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoElectricRecharge.class);
                break;

            case SPECTRANET_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoSpectranetRecharge.class);
                break;

            case SMILE_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoSmileRecharge.class);
                break;

            case DSTV_LABEL:
            case GOTV_LABEL:
            case STARTIMES_LABEL:
                recharge = ApplicationContextProvider.getBean(RingoDstvRecharge.class);
        }

        return recharge;
    }
}
