package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.factory.EKEDPRechargeFactory;
import io.factorialsystems.msscprovider.wsdl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServicesImpl implements Services{
    private final EKEDProperties properties;
    private final ObjectFactory objectFactory;
    private final WebServiceTemplate webServiceTemplate;
    private final SessionImpl sessionImpl;

    private String session = null;

    @Override
    public BigDecimal getBalance() {
        GetBalance balance = new GetBalance();
        JAXBElement<GetBalance> jaxbBalance = objectFactory.createGetBalance(balance);
        JAXBElement<GetBalanceResponse> jaxbResponse = (JAXBElement<GetBalanceResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbBalance, callbackHeader((session == null ? sessionImpl.getSession() : session)));

        if(jaxbResponse==null || jaxbResponse.getValue()==null || jaxbResponse.getValue().getResponse() ==null){
            //Todo("Log error for reference sake")
            throw new RuntimeException("And Error Occurred, Try again later.");
        }

        WalletResponse response = jaxbResponse.getValue().getResponse();

        if(response.getRetn() == Responses.SUCCESS_CODE.getCode()) return response.getBalance();

        if(canRefreshSession(response.getRetn()))
            return this.getBalance();

        throw new RuntimeException("Error Getting Balance for Eko Distribution Company");
    }

    @Override
    public CustomerInfo validateCustomer(String meterOrAccountId) {
        ValidateCustomer validateCustomer = new ValidateCustomer();
        validateCustomer.setTenantId(properties.getPartnerId());
        validateCustomer.setAccountOrMeterNumber(meterOrAccountId);

        JAXBElement<ValidateCustomer> jaxbCustomer = objectFactory.createValidateCustomer(validateCustomer);
        JAXBElement<ValidateCustomerResponse> jaxbResponse =  (JAXBElement<ValidateCustomerResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbCustomer, callbackHeader(session == null ? sessionImpl.getSession() : session));

        if(jaxbResponse==null || jaxbResponse.getValue()==null || jaxbResponse.getValue().getResponse() ==null){
            //Todo("Log error for reference sake")
            throw new RuntimeException("And Error Occurred, Try again later.");
        }

        CustomerInfoResponse response = jaxbResponse.getValue().getResponse();

        if(response.getRetn() == Responses.SUCCESS_CODE.getCode()) return response.getCustomerInfo();

        if(canRefreshSession(response.getRetn()))
            return this.validateCustomer(meterOrAccountId);

        throw new RuntimeException("Error Getting Customer Info for Eko Distribution Company");
    }

    @Override
    public OrderDetails getOrderDetails(String paymentId) {
        GetOrderDetailsV2 orderDetailsV2 = new GetOrderDetailsV2();
        orderDetailsV2.setPaymentReference(paymentId);
        orderDetailsV2.setTenantId(properties.getPartnerId());

        JAXBElement<GetOrderDetailsV2> jaxbOrderDetails = objectFactory.createGetOrderDetailsV2(orderDetailsV2);
        JAXBElement<GetOrderDetailsV2Response> jaxbResponse =  (JAXBElement<GetOrderDetailsV2Response>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbOrderDetails, callbackHeader(session == null ? sessionImpl.getSession() : session));

        if(jaxbResponse==null || jaxbResponse.getValue()==null || jaxbResponse.getValue().getResponse() ==null){
            //Todo("Log error for reference sake")
            throw new RuntimeException("And Error Occurred, Try again later.");
        }

        OrderDetailsResponse response = jaxbResponse.getValue().getResponse();

        if(response.getRetn() == Responses.SUCCESS_CODE.getCode()) return response.getOrderDetails();

        if(canRefreshSession(response.getRetn()))
            return this.getOrderDetails(paymentId);

        throw new RuntimeException("Error Getting Order Details for Eko Distribution Company");
    }

    @Override
    public void reversePayment(String paymentId) {
        NotifyForReversal notifyForReversal = new NotifyForReversal();
        notifyForReversal.setPaymentReference(paymentId);
        notifyForReversal.setTenantId(properties.getPartnerId());

        JAXBElement<NotifyForReversal> jaxbReversal = objectFactory.createNotifyForReversal(notifyForReversal);
        JAXBElement<NotifyForReversalResponse> jaxbResponse =  (JAXBElement<NotifyForReversalResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbReversal, callbackHeader(session == null ? sessionImpl.getSession() : session));

        if(jaxbResponse==null || jaxbResponse.getValue()==null || jaxbResponse.getValue().getResponse() ==null){
            //Todo("Log error for reference sake")
            throw new RuntimeException("And Error Occurred, Try again later.");
        }

        NotifyReversalResponse response = jaxbResponse.getValue().getResponse();

        if(response.getRetn() == Responses.SUCCESS_CODE.getCode()){
            log.info("Reversal Response Retn: {}, Desc: {}", response.getRetn(), response.getDesc());
        }else{
            log.info("Reversal Response:", response);
        }

    }

    @Override
    public void validatePayment(String customerId, String accountType) {
        ValidatePayment validatePayment = new ValidatePayment();
        validatePayment.setTenantId(properties.getPartnerId());

        ValidationParams params = new ValidationParams();
        params.setAccountType(Objects.equals(accountType, "") ?"OFFLINE_PREPAID":accountType);
        params.setCustomerId(Objects.equals(accountType, "") ?"45700863561":accountType);

        validatePayment.setParams(params);
        validatePayment.setTenantId(properties.getPartnerId());

        JAXBElement<ValidatePayment> jaxbValidate = objectFactory.createValidatePayment(validatePayment);
        JAXBElement<ValidatePaymentResponse> jaxbResponse =  (JAXBElement<ValidatePaymentResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbValidate, callbackHeader(session == null ? sessionImpl.getSession() : session));

        ValidatePaymentResponseV2 paymentResponseV2 = jaxbResponse.getValue().getResponse();

        if (paymentResponseV2 != null) {
            log.info("return: {}, Description: {}", paymentResponseV2.getRetn(), paymentResponseV2.getDesc());

            if (paymentResponseV2.getRetn() == 0) {
                ValidationInfo validationInfo = paymentResponseV2.getValidationInfo();

                log.info("ValidationInfo Amount: {}, CustomerId: {}", validationInfo.getAmount(), validationInfo.getCustomerId());
            }
        }
    }

    @Override
    public void payPostPaidBill(SingleRechargeRequest request) {
        CustomerInfo customerInfo = validateCustomer(request.getRecipient());

        if (customerInfo != null) {
            performRecharge(request);
        } else {
            log.error("Recharge Error unable to Validate Customer");
        }
    }

    @Override
    public OrderDetails performRecharge(SingleRechargeRequest request) {
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
        JAXBElement<ChargeWalletV2Response> jaxbResponse = (JAXBElement<ChargeWalletV2Response>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbCharge, callbackHeader((session == null ? sessionImpl.getSession() : session)));

        if(jaxbResponse==null || jaxbResponse.getValue()==null || jaxbResponse.getValue().getResponse() ==null){
            //Todo("Log error for reference sake")
            throw new RuntimeException("And Error Occurred, Try again later.");
        }

        WalletResponse response = jaxbResponse.getValue().getResponse();

        if(response.getRetn() == Responses.SUCCESS_CODE.getCode()) {
            // Retrieve the Token
            if (request.getAccountType().equals(EKEDPRechargeFactory.ACCOUNT_TYPE_PREPAID)) {
                OrderDetails details = response.getOrderDetails();

                if (details != null) {
                    if (details.getStatus().value().equals("AWAITING_SERVICE_PROVIDER")) {
                        try {
                            sleep(5000);
                            return getOrderDetails(request.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    log.info("Token: {}", details.getTokenData().getStdToken().getValue());
                }
            }
        }

        if(canRefreshSession(response.getRetn()))
            return this.performRecharge(request);

        return null;
    }

    private WebServiceMessageCallback callbackHeader(String session) {
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

    private boolean canRefreshSession(int responseCode){
        if(responseCode == Responses.INVALID_SESSION_CODE_0.getCode() || responseCode == Responses.INVALID_SESSION_CODE_1.getCode()){

            if(session!=null) throw new RuntimeException("Unable to refresh session / session Error");

            session = sessionImpl.reFreshSession();
            return true;
        }

        return false;
    }
}