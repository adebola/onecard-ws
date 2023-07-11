package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.EKEDPRechargeFactory;
import io.factorialsystems.msscprovider.utils.Constants;
import io.factorialsystems.msscprovider.wsdl.ekedp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.math.BigDecimal;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class EKEDPElectricRecharge implements Recharge, ParameterCheck, Balance {
    private final EKEDProperties properties;
    private final ObjectFactory objectFactory;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        String session = getSession();

        if (session == null) {
            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_GATEWAY)
                    .message("Unable to acquire Session from EKEDP Gateway, please try later")
                    .build();
        }

        if (!login(session)) {
            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_GATEWAY)
                    .message("Login Failure to EKEDP Gateway, please try later")
                    .build();
        }

        if (!performRecharge(session, request)) {
            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_GATEWAY)
                    .message("Login Failure to EKEDP Gateway, please try later")
                    .build();
        }

        return RechargeStatus.builder()
                .status(HttpStatus.OK)
                .message("Recharge Successful")
                .build();
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null &&
                request.getRecipient() != null &&
                request.getServiceCost() != null &&
                request.getServiceCost().compareTo(Constants.MINIMUM_RECHARGE_VALUE) > 0 &&
                request.getAccountType() != null;
    }

    private String getSession() {
        StartSession session = new StartSession();
        session.setPartnerId(properties.getPartnerId());
        session.setAccessKey(properties.getAccessKey());

        JAXBElement<StartSession> jaxbElement = objectFactory.createStartSession(session);
        JAXBElement<StartSessionResponse> response =
                (JAXBElement<StartSessionResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

        if (response != null && response.getValue() != null) {
            StartSessionResponse sessionResponse = response.getValue();
            return sessionResponse.getResponse().getSession();
        }

        return null;
    }

    private Boolean login(String session) {
        Login login = new Login();
        login.setEmail(properties.getMail());
        login.setAccessKey(properties.getAccessKey());
        JAXBElement<Login> jaxbLogin = objectFactory.createLogin(login);

        JAXBElement<LoginResponse> jaxbResponse = (JAXBElement<LoginResponse>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbLogin, callback(session));

        if (jaxbResponse != null && jaxbResponse.getValue() != null) {
            LoginResponse loginResponse = jaxbResponse.getValue();

            if (loginResponse.getResponse().getDesc().equals("Request Successful") && loginResponse.getResponse().getRetn() == 0) {
                return true;
            }
        }

        return false;
    }

    private void logout(String session) {
        Logout logout = new Logout();
        JAXBElement<Logout> jaxbLogout = objectFactory.createLogout(logout);

        JAXBElement<LogoutResponse> jaxbResponse =
                (JAXBElement<LogoutResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbLogout, callback(session));
    }

    private Boolean performRecharge(String session, SingleRechargeRequest request) {

        if (request.getAccountType() == null || EKEDPRechargeFactory.codeMapper.get(request.getAccountType()) == null) {
            throw new RuntimeException("Account Type not specified or invalid, please ensure it is set to either \"prepaid\" or \"postpaid\"");
        }

        final String accountType = EKEDPRechargeFactory.codeMapper.get(request.getAccountType());

        TransactionParams.ExtraData.Entry entryAccountType = new TransactionParams.ExtraData.Entry();
        entryAccountType.setKey("accountType");
        entryAccountType.setValue(accountType);

        TransactionParams.ExtraData.Entry entryMeter = new TransactionParams.ExtraData.Entry();

        if (request.getAccountType().equals(EKEDPRechargeFactory.ACCOUNT_TYPE_PREPAID)) {
            entryMeter.setKey("meterNumber");
        } else {
            entryMeter.setKey("accountNumber");
        }

        entryMeter.setValue(request.getRecipient());

        TransactionParams.ExtraData.Entry entryPurpose = new TransactionParams.ExtraData.Entry();
        entryPurpose.setKey("purpose");
        entryPurpose.setValue("Bill payment");

        TransactionParams.ExtraData.Entry entryPartner = new TransactionParams.ExtraData.Entry();
        entryPartner.setKey("partnerChannel");
        entryPartner.setValue(properties.getPartnerId());

        TransactionParams.ExtraData extraData = new TransactionParams.ExtraData();
        extraData.getEntry().add(entryAccountType);
        extraData.getEntry().add(entryMeter);
        extraData.getEntry().add(entryPurpose);
        extraData.getEntry().add(entryPartner);

        TransactionParams parameters = new TransactionParams();
        parameters.setExtraData(extraData);
        parameters.setAmount(request.getServiceCost().doubleValue());
        parameters.setPaymentReference(request.getId());

        ChargeWalletV2 chargeWalletV2 = new ChargeWalletV2();
        chargeWalletV2.setParams(parameters);

        JAXBElement<ChargeWalletV2> jaxbCharge = objectFactory.createChargeWalletV2(chargeWalletV2);
        JAXBElement<ChargeWalletV2Response> jaxbResponse =
                (JAXBElement<ChargeWalletV2Response>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbCharge, callback(session));

        WalletResponse response = jaxbResponse.getValue().getResponse();

        if (response.getRetn() == 0) {
            log.info("Payment Reference {}", request.getId());
            log.info("Response Status: {} Balance {} OrderId {}", response.getRetn(), response.getBalance(), response.getOrderId());

            // Retrieve the Token
            if (request.getAccountType().equals(EKEDPRechargeFactory.ACCOUNT_TYPE_PREPAID)) {
                GetOrderDetailsV2Response orderDetailsV2Response = getOrderDetails(session, request.getId());

                OrderDetails details = orderDetailsV2Response.getResponse().getOrderDetails();

                if (details != null) {
                    if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER")) {
                        try {
                            sleep(5000);
                            orderDetailsV2Response = getOrderDetails(session, request.getId());
                            details = orderDetailsV2Response.getResponse().getOrderDetails();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    log.info("Token: {}", details.getTokenData().getStdToken().getValue());
                }
            }

            return true;
        }

        log.error("Performing Recharge Code: {}, Description: {}", response.getRetn(), response.getDesc());
        return false;
    }

    private WebServiceMessageCallback callback(String session) {
        return webServiceMessage -> {

            try {
                SOAPMessage soapMessage = ((SaajSoapMessage) webServiceMessage).getSaajMessage();
                SOAPHeader soapHeader = soapMessage.getSOAPHeader();

                SOAPHeaderElement actionElement =
                        soapHeader.addHeaderElement(new QName("http://soap.convergenceondemand.net/TMP/", "sessionId", "tmp"));
                actionElement.setMustUnderstand(false);
                actionElement.setTextContent(session);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        };
    }

    @Override
    public BigDecimal getBalance() {
        String session = getSession();

        if (session == null) {
            throw new RuntimeException("Error acquiring EKEDP Session");
        }

        if (!login(session)) {
            throw new RuntimeException("Login Error");
        }

        GetBalance balance = new GetBalance();
        JAXBElement<GetBalance> jaxbBalance = objectFactory.createGetBalance(balance);
        JAXBElement<GetBalanceResponse> jaxbResponse = (JAXBElement<GetBalanceResponse>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbBalance, callback(session));

        logout(session);

        if (jaxbResponse != null && jaxbResponse.getValue() != null && jaxbResponse.getValue().getResponse() != null && jaxbResponse.getValue().getResponse().getBalance() != null) {
            return jaxbResponse.getValue().getResponse().getBalance();
        }

        throw new RuntimeException("Error Getting Balance for Eko Distribution Company");
    }

    private CustomerInfo validateCustomer(String session, String meteroraccountId) {
        ValidateCustomer validateCustomer = new ValidateCustomer();
        validateCustomer.setTenantId(properties.getPartnerId());
        validateCustomer.setAccountOrMeterNumber(meteroraccountId);

        JAXBElement<ValidateCustomer> jaxbCustomer = objectFactory.createValidateCustomer(validateCustomer);
        JAXBElement<ValidateCustomerResponse> jaxbResponse =  (JAXBElement<ValidateCustomerResponse>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbCustomer, callback(session));

        if (jaxbResponse.getValue() != null && jaxbResponse.getValue().getResponse() != null && jaxbResponse.getValue().getResponse().getRetn() == 0) {
            CustomerInfo customerInfo = jaxbResponse.getValue().getResponse().getCustomerInfo();

            log.info("CustomerInfo Name: {}, Address: {}",customerInfo.getName(), customerInfo.getAddress());
            return jaxbResponse.getValue().getResponse().getCustomerInfo();
        } else {
            CustomerInfoResponse response = jaxbResponse.getValue().getResponse();
            log.error("Error Validating Customer Code {}, Message {}", response.getRetn(),  response.getDesc());
        }

        return null;
    }

    private GetOrderDetailsV2Response getOrderDetails(String session, String paymentId) {
        GetOrderDetailsV2 orderDetailsV2 = new GetOrderDetailsV2();
        orderDetailsV2.setPaymentReference(paymentId);
        orderDetailsV2.setTenantId(properties.getPartnerId());

        JAXBElement<GetOrderDetailsV2> jaxbOrderDetails = objectFactory.createGetOrderDetailsV2(orderDetailsV2);
        JAXBElement<GetOrderDetailsV2Response> jaxbResponse =  (JAXBElement<GetOrderDetailsV2Response>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbOrderDetails, callback(session));

        return jaxbResponse.getValue();
    }

    public void reversePayment(String paymentId) {
        String session = getSession();
        login(session);

        NotifyForReversal notifyForReversal = new NotifyForReversal();
        notifyForReversal.setPaymentReference(paymentId);
        notifyForReversal.setTenantId(properties.getPartnerId());

        JAXBElement<NotifyForReversal> jaxbReversal = objectFactory.createNotifyForReversal(notifyForReversal);
        JAXBElement<NotifyForReversalResponse> jaxbResponse =  (JAXBElement<NotifyForReversalResponse>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbReversal, callback(session));

        NotifyReversalResponse response = jaxbResponse.getValue().getResponse();
        log.info("Reversal Response Retn: {}, Desc: {}", response.getRetn(), response.getDesc());
        logout(session);
    }

    public void validatePayment(String id) {
        String session = getSession();
        login(session);

        ValidatePayment validatePayment = new ValidatePayment();
        validatePayment.setTenantId(properties.getPartnerId());

        ValidationParams params = new ValidationParams();
        params.setAccountType("OFFLINE_PREPAID");
        params.setCustomerId("45700863561");

        validatePayment.setParams(params);
        validatePayment.setTenantId(properties.getPartnerId());

        JAXBElement<ValidatePayment> jaxbValidate = objectFactory.createValidatePayment(validatePayment);
        JAXBElement<ValidatePaymentResponse> jaxbResponse =  (JAXBElement<ValidatePaymentResponse>) webServiceTemplate
                .marshalSendAndReceive(properties.getUrl(), jaxbValidate, callback(session));

        ValidatePaymentResponseV2 paymentResponseV2 = jaxbResponse.getValue().getResponse();

        if (paymentResponseV2 != null) {
            log.info("return: {}, Description: {}", paymentResponseV2.getRetn(), paymentResponseV2.getDesc());

            if (paymentResponseV2.getRetn() == 0) {
                ValidationInfo validationInfo = paymentResponseV2.getValidationInfo();

                log.info("ValidationInfo Amount: {}, CustomerId: {}", validationInfo.getAmount(), validationInfo.getCustomerId());
            }
        }


        logout(session);
    }

    public void payPostPaidBill(SingleRechargeRequest request) {
        String session = getSession();

        if (session == null) {
            throw new RuntimeException("Error acquiring EKEDP Session");
        }

        if (!login(session)) {
            throw new RuntimeException("Login Error");
        }

        CustomerInfo customerInfo = validateCustomer(session, request.getRecipient());

        if (customerInfo != null) {
            performRecharge(session, request);
        } else {
            log.error("Recharge Error unable to Validate Customer");
        }

        logout(session);
    }
}