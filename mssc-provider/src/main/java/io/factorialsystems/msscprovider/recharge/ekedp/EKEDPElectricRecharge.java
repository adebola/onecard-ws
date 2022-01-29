package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.wsdl.*;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class EKEDPElectricRecharge implements Recharge {
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

    private String getSession() {

        StartSession session = new StartSession();
        session.setPartnerId(properties.getPartnerId());
        session.setAccessKey(properties.getAccessKey());

        JAXBElement<StartSession> jaxbElement = objectFactory.createStartSession(session);
        JAXBElement<StartSessionResponse> response = (JAXBElement<StartSessionResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

        if (response != null && response.getValue() != null) {
            StartSessionResponse sessionResponse = response.getValue();
            return sessionResponse.getResponse().getSession();
        }

        return null;
    }

    private Boolean login(String session) {
//        JAXBElement<String> jaxbSession = objectFactory.createSessionId(session);

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

    private Boolean performRecharge(String session, SingleRechargeRequest request) {
        TransactionParams.ExtraData.Entry entryAccountType = new TransactionParams.ExtraData.Entry();
        entryAccountType.setKey("accountType");
        entryAccountType.setValue("OFFLINE_PREPAID");

        TransactionParams.ExtraData.Entry entryMeter = new TransactionParams.ExtraData.Entry();
        entryMeter.setKey("meterNumber");
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

//        TransactionParams parameters = new TransactionParams();
//        parameters.setExtraData(extraData);
//        parameters.setAmount(request.getServiceCost().doubleValue());
//
//        ChargeWalletV2 chargeWalletV2 = new ChargeWalletV2();
//        chargeWalletV2.setParams(parameters);
//
//        JAXBElement<ChargeWalletV2> jaxbCharge = objectFactory.createChargeWalletV2(chargeWalletV2);
//        JAXBElement<ChargeWalletV2Response> jaxbResponse = (JAXBElement<ChargeWalletV2Response>) webServiceTemplate
//                .marshalSendAndReceive(properties.getUrl(), jaxbCharge, callback(session));

        return true;
    }

    private WebServiceMessageCallback callback(String session) {
        return webServiceMessage -> {

            try {
                SOAPMessage soapMessage = ((SaajSoapMessage) webServiceMessage).getSaajMessage();
                SOAPHeader soapHeader = soapMessage.getSOAPHeader();

                SOAPHeaderElement actionElement = soapHeader.addHeaderElement(new QName("http://soap.convergenceondemand.net/TMP/", "sessionId", "tmp"));
                actionElement.setMustUnderstand(false);
                actionElement.setTextContent(session);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        };
    }
}


//<soapenv:Header>
//    <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
//        <wsse:UsernameToken xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
//            <wsse:Username>abc</wsse:Username>
//            <wsse:Password>abc</wsse:Password>
//        </wsse:UsernameToken>
//    </wsse:Security>
//</soapenv:Header>
//
//<soapenv:Header>
//    <tmp:sessionId>6ABA1505086A30CC795E3824864D30B05733B34E63799B5C2AD1E9E0A6717E78</tmp :sessionId>
//</soapenv:Header>

//                        SoapHeader soapHeader = ((SoapMessage) webServiceMessage).getSoapHeader();
//                        Map<String, String> mapRequest = new HashMap();
//                        mapRequest.put("sessionId", session);

// try {
//                                    SaajSoapMessage saajSoapMessage = (SaajSoapMessage) webServiceMessage;
//                                    SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
//                                    SOAPPart soapPart = soapMessage.getSOAPPart();
//                                    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
//                                    SOAPHeader soapHeader = soapEnvelope.getHeader();
//
//                                    soapHeader.addHeaderElement((Name)objectFactory.createSessionId(session));
//                                    soapMessage.saveChanges();
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
