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
//        request.setRecipient("0244140270-01");
        request.setRecipient("45700863561");
        request.setAccountType("prepaid");
        request.setServiceCost(new BigDecimal(500));
        recharge.payPostPaidBill(request);
    }

    @Test
    public void reverse() {
        String id = "690722a0-e12f-4101-a436-b31828c81efd";
        recharge.reversePayment(id);
    }

    @Test
    public void validatePayment() {
        String id = "690722a0-e12f-4101-a436-b31828c81efd";
        recharge.validatePayment(id);
    }
}
