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
    void payPostPaidGreaterThanHundred() {
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("postpaid");

//        request.setRecipient("0244140270-01");
        request.setRecipient("0312214672-01");
        request.setServiceCost(new BigDecimal(120));
        recharge.payPostPaidBill(request);
    }

    @Test
    void payPostPaidLessThanHundred() {
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("postpaid");

        request.setRecipient("0244140270-01");
        request.setServiceCost(new BigDecimal(90));
        recharge.payPostPaidBill(request);
    }

    @Test
    void payPostPaidBillOfflinePrepaid() {
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("prepaid");

        request.setRecipient("0244140270-01");
        request.setServiceCost(new BigDecimal(90));
        recharge.payPostPaidBill(request);
    }

    @Test
    void prepaidTokenSuccessfulReQuery() {
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("prepaid");

        request.setRecipient("45700863561");
        request.setServiceCost(new BigDecimal(900));
        recharge.payPostPaidBill(request);
    }

    @Test
    void prepaidTokenFailedReQuery() {

        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
        // Before Invoking this function for Re-Query to fail
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("prepaid");

        request.setRecipient("45700863561");
        request.setServiceCost(new BigDecimal(900));
        recharge.payPostPaidBill(request);
    }

    @Test
    void prepaidTokenVendLessThan900() {

        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
        // Before Invoking this function for Re-Query to fail
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("prepaid");

        request.setRecipient("45700863561");
        request.setServiceCost(new BigDecimal(800));
        recharge.payPostPaidBill(request);
    }

    @Test
    void prepaidTokenVendFailed() {

        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
        // Before Invoking this function for Re-Query to fail
        SingleRechargeRequest request = new SingleRechargeRequest();
        request.setId(UUID.randomUUID().toString());
        request.setAccountType("prepaid");

        request.setRecipient("1234567890");
        request.setServiceCost(new BigDecimal(800));
        recharge.payPostPaidBill(request);
    }


    @Test
    public void reverse() {
        String id = "45ec1adf-b4d7-4f1e-a60e-d528084effea";
        recharge.reversePayment(id);
    }

    @Test
    public void validatePayment() {
        String id = "45ec1adf-b4d7-4f1e-a60e-d528084effea";
        recharge.validatePayment(id);
    }
}
