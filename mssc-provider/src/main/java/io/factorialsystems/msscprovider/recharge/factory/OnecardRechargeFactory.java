package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.onecard.OnecardAirtimeRecharge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardRechargeFactory extends AbstractFactory {
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
        return onecardAirtimeRecharge;
    }

    @Override
    public DataEnquiry getPlans(String action) {
        return onecardAirtimeRecharge;
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {
        throw new RuntimeException("Onecard ExtraDataPlane Not Implemented");
    }

    @Override
    public ParameterCheck getCheck(String action) {
        return onecardAirtimeRecharge;
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
