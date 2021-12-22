package io.factorialsystems.msscprovider.recharge.ekedp;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class EKEDPElectricRechargeTest {

    @Autowired
    private EKEDPElectricRecharge recharge;

    @Test
    void recharge() {
//        RechargeRequest request = new RechargeRequest();
//        request.setRecipient("00000000000000000");
//        recharge.recharge(new RechargeRequest());
    }
}
