package io.factorialsystems.msscprovider.recharge.ringo;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class RingoDataRechargeTest {

    @Autowired
    private RingoDataRecharge recharge;

    @Test
    void rechargeTest() {
//        RechargeRequest request = new RechargeRequest();
//        request.setRecipient("2348030873116");
//        request.setServiceCode("MTN-DATA");
//        request.setProductId("MT1");
//
//        RechargeStatus status = recharge.recharge(request);
//        assertEquals(status.getStatus(), HttpStatus.OK);
    }
}
