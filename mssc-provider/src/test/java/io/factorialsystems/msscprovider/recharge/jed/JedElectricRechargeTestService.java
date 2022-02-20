package io.factorialsystems.msscprovider.recharge.jed;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class JedElectricRechargeTestService {

    @Autowired
    private JedElectricRecharge recharge;

    @Test
    void recharge() {
//        RechargeRequest request = new RechargeRequest();
//        request.setRecipient("44000316354");
//        request.setTelephone("08055572307");
//        request.setServiceCost(new BigDecimal(1250));

//        RechargeStatus status = recharge.recharge(request);
//        assertEquals(status.getStatus(), HttpStatus.OK);
    }
}
