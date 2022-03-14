package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.wsdl.CustomerInfo;
import io.factorialsystems.msscprovider.wsdl.OrderDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import java.math.BigDecimal;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(locations = "/test-context.xml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EKEDPElectricRechargeTest {

    @Autowired
    @Qualifier("serviceImpl")
    private ServicesImpl services;

    @Autowired
    @Qualifier("sessionImpl")
    private SessionImpl session;

    @Test
    @DisplayName("Gets Session and also performs a Login immediately after getting Session.")
    @Order(1)
    public void testGetSessionNotNull(){
        String session = this.session.getSession();
        assertNotNull(session, "Fail to get session.");
        System.out.println("SESSION "+session);
    }

    @Test
    @DisplayName("Gets Merchant Account Balance.")
    @Order(2)
    public void testGetBalanceNotNull(){
        BigDecimal balance = this.services.getBalance();
        assertTrue(this.services.getBalance() !=null, "Fail to get merchant account balance.");
        System.out.println("BALANCE "+balance);
    }

    @Test
    @DisplayName("Gets Customer Info for validation purpose.")
    @Order(3)
    public void testValidateCustomerNotNull(){
        CustomerInfo customer = this.services.validateCustomer("0312214672-01", "EKEDP");
        assertTrue(customer!=null, "Fail to get customer Info.");
        System.out.println("Name: "+ customer.getName());
        System.out.println("Type: "+ customer.getAccountType());
        System.out.println("Address: "+ customer.getAddress());
    }

    @Test
    @DisplayName("Performs a recharge, it's can either be postpaid or prepaid, depending on the accountType parsed in the SingleRechargeRequest.")
    @Order(4)
    public void testPerformRechargeNotNull(){
        SingleRechargeRequest singleRechargeRequest = new SingleRechargeRequest();
        singleRechargeRequest.setServiceCost(BigDecimal.valueOf(100.0));
        singleRechargeRequest.setRecipient("0312214672-01");
        singleRechargeRequest.setAccountType("postpaid");

        int id = new Random().nextInt(1000);
        singleRechargeRequest.setId(String.valueOf(id));

        OrderDetails orderDetails = this.services.performRecharge(singleRechargeRequest);
        System.out.println("orderDetails "+ orderDetails);
        assertTrue(orderDetails!=null, "Fail to recharge.");
    }



//    @Test
//    void payPostPaidBillOfflinePrepaid() {
//        SingleRechargeRequest request = new SingleRechargeRequest();
//        request.setId(UUID.randomUUID().toString());
//        request.setAccountType("prepaid");
//
//        request.setRecipient("0244140270-01");
//        request.setServiceCost(new BigDecimal(90));
//        recharge.payPostPaidBill(request);
//    }
//
//    @Test
//    void prepaidTokenSuccessfulReQuery() {
//        SingleRechargeRequest request = new SingleRechargeRequest();
//        request.setId(UUID.randomUUID().toString());
//        request.setAccountType("prepaid");
//
//        request.setRecipient("45700863561");
//        request.setServiceCost(new BigDecimal(900));
//        recharge.payPostPaidBill(request);
//    }
//
//    @Test
//    void prepaidTokenFailedReQuery() {
//
//        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
//        // Before Invoking this function for Re-Query to fail
//        SingleRechargeRequest request = new SingleRechargeRequest();
//        request.setId(UUID.randomUUID().toString());
//        request.setAccountType("prepaid");
//
//        request.setRecipient("45700863561");
//        request.setServiceCost(new BigDecimal(900));
//        recharge.payPostPaidBill(request);
//    }
//
//    @Test
//    void prepaidTokenVendLessThan900() {
//
//        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
//        // Before Invoking this function for Re-Query to fail
//        SingleRechargeRequest request = new SingleRechargeRequest();
//        request.setId(UUID.randomUUID().toString());
//        request.setAccountType("prepaid");
//
//        request.setRecipient("45700863561");
//        request.setServiceCost(new BigDecimal(800));
//        recharge.payPostPaidBill(request);
//    }
//
//    @Test
//    void prepaidTokenVendFailed() {
//
//        // Comment Out try / catch below  if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER"))
//        // Before Invoking this function for Re-Query to fail
//        SingleRechargeRequest request = new SingleRechargeRequest();
//        request.setId(UUID.randomUUID().toString());
//        request.setAccountType("prepaid");
//
//        request.setRecipient("1234567890");
//        request.setServiceCost(new BigDecimal(800));
//        recharge.payPostPaidBill(request);
//    }
//
//
//    @Test
//    public void reverse() {
//        String id = "45ec1adf-b4d7-4f1e-a60e-d528084effea";
//        recharge.reversePayment(id);
//    }
//
//    @Test
//    public void validatePayment() {
//        String id = "45ec1adf-b4d7-4f1e-a60e-d528084effea";
//        recharge.validatePayment(id);
//    }
}