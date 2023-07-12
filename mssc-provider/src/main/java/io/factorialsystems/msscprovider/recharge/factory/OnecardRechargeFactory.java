package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.onecard.OnecardAirtimeRecharge;
import io.factorialsystems.msscprovider.recharge.onecard.OnecardDataRecharge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.factorialsystems.msscprovider.utils.Constants.AIRTIME_LABEL;
import static io.factorialsystems.msscprovider.utils.Constants.DATA_LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardRechargeFactory extends AbstractFactory {
    private final OnecardDataRecharge onecardDataRecharge;
    private final OnecardAirtimeRecharge onecardAirtimeRecharge;

    public static final Map<String, String> codeMapper = new HashMap<>();

    static {
        codeMapper.put("MTN-AIRTIME", "2");
        codeMapper.put("AIRTEL-AIRTIME", "3");
        codeMapper.put("GLO-AIRTIME", "5");
        codeMapper.put("9MOBILE-AIRTIME", "4");
        codeMapper.put("MTN-DATA", "24");
        codeMapper.put("AIRTEL-DATA", "25");
        codeMapper.put("GLO-DATA", "27");
        codeMapper.put("9MOBILE-DATA", "26");
        codeMapper.put("EKEDP", "12");
        codeMapper.put("JED", "13");
        codeMapper.put("DSTV", "10");
        codeMapper.put("GOTV", "11");
        codeMapper.put("STARTIMES", "9");
        codeMapper.put("SPECTRANET-DATA", "7");
        codeMapper.put("SMILE-DATA", "8");
    }

    @Override
    public Recharge getRecharge(String action) {

        switch (action) {
            case AIRTIME_LABEL:
               return onecardAirtimeRecharge;

            case DATA_LABEL:
                return onecardDataRecharge;
        }

        return null;
    }

    @Override
    public DataEnquiry getPlans(String action) {
        return onecardDataRecharge;
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {
        throw new RuntimeException("Onecard ExtraDataPlan Not Implemented");
    }

    @Override
    public ParameterCheck getCheck(String action) {
        switch (action) {
            case AIRTIME_LABEL:
                return onecardAirtimeRecharge;

            case DATA_LABEL:
                return onecardDataRecharge;
        }

        return null;
    }

    @Override
    public Balance getBalance() {
        return onecardAirtimeRecharge;
    }

    @Override
    public ReQuery getReQuery() {
       throw new RuntimeException("Onecard Requery Not Implemented");
    }
}
