package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.ringo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.factorialsystems.msscprovider.utils.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoRechargeFactory extends AbstractFactory {
    private final RingoAirtimeRecharge airtimeRecharge;
    private final RingoSpectranetRecharge spectranetRecharge;
    private final RingoMobileDataRecharge mobileDataRecharge;
    private final RingoElectricRecharge electricRecharge;
    private final RingoDstvRecharge dstvRecharge;
    private final RingoSmileRecharge smileRecharge;

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
                enquiry = mobileDataRecharge;
                break;

            case SPECTRANET_LABEL:
            case SPECTRANET_DATA_LABEL:
                enquiry =  spectranetRecharge;
                break;

            case SMILE_LABEL:
            case SMILE_DATA_LABEL:
                enquiry =  smileRecharge;
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
                extraEnquiry =  dstvRecharge;
                break;

            case EKEDP_LABEL:
                extraEnquiry = electricRecharge;
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
        return airtimeRecharge;
    }

    @Override
    public ReQuery getReQuery() {
        return airtimeRecharge;
    }

    private Recharge getClass(String action) {
        Recharge recharge = null;

        switch (action) {
            case AIRTIME_LABEL:
                recharge = airtimeRecharge;
                break;

            case DATA_LABEL:
                recharge = mobileDataRecharge;
                break;

            case ELECTRICITY_LABEL:
                recharge = electricRecharge;
                break;

            case SPECTRANET_LABEL:
                recharge = spectranetRecharge;
                break;

            case SMILE_LABEL:
                recharge = smileRecharge;
                break;

            case DSTV_LABEL:
            case GOTV_LABEL:
            case STARTIMES_LABEL:
                recharge = dstvRecharge;
        }

        return recharge;
    }
}
