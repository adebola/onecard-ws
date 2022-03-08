package io.factorialsystems.msscprovider.recharge.ekedp;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionImpl implements Session{
    private final EKEDProperties properties;
    private final ObjectFactory objectFactory;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public String startSession() {
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

    @Override
    public String getSession() {
        //Todo("Retrieve session from Cache and return it, else proceed to below.")

        String session = startSession();
        if(session == null) throw new RuntimeException("Could not get session");

        if(logIn(session)) return session;

        return null;
    }

    @Override
    public String reFreshSession() {
        //Todo("Remove session from Cache, if available, and proceed to below.")
        return getSession();
    }

    @Override
    public Boolean logIn(String session) {
        Login login = new Login();
        login.setEmail(properties.getMail());
        login.setAccessKey(properties.getAccessKey());
        JAXBElement<Login> jaxbLogin = objectFactory.createLogin(login);

        JAXBElement<LoginResponse> jaxbResponse = (JAXBElement<LoginResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbLogin, callbackHeader(session));

        if(jaxbResponse==null || jaxbResponse.getValue() == null) return false;

        LoginResponse loginResponse = jaxbResponse.getValue();
        return loginResponse.getResponse().getDesc().equals("Request Successful") && loginResponse.getResponse().getRetn() == 0;
    }

    @Override
    public void logOut(String session) {
        Logout logout = new Logout();
        JAXBElement<Logout> jaxbLogout = objectFactory.createLogout(logout);

        JAXBElement<LogoutResponse> jaxbResponse = (JAXBElement<LogoutResponse>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbLogout, callbackHeader(session));

//        if(jaxbResponse==null || jaxbResponse.getValue() == null) return false;
//        LogoutResponse logoutResponse = jaxbResponse.getValue();
//        return logoutResponse.getResponse().getDesc().equals(Responses.SUCCESS.getStatus()) && logoutResponse.getResponse().getRetn() == Responses.SUCCESS_CODE.getCode();
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
}