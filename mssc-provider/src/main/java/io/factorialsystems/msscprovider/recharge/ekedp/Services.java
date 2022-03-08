package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.wsdl.CustomerInfo;
import io.factorialsystems.msscprovider.wsdl.OrderDetails;

import java.math.BigDecimal;

public interface Services {
    BigDecimal getBalance();
    CustomerInfo validateCustomer(String meterOrAccountId);
    OrderDetails getOrderDetails(String paymentId);
    void reversePayment(String paymentId);
    void validatePayment(String customerId, String accountType);
    void payPostPaidBill(SingleRechargeRequest request);
    OrderDetails performRecharge(SingleRechargeRequest request);
}