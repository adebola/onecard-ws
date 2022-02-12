package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

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

    @Test
    void getBalance() {
        BigDecimal balance = recharge.getBalance();
    }

    @Test
    void payPostPaidBill() {
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setRecipient("0244140270-01");
        request.setAccountType("postpaid");
        request.setServiceCost(new BigDecimal(200));
        recharge.payPostPaidBill(request);
    }
}
