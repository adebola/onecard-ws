package io.factorialsystems.msscprovider.recharge.mtn;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.wsdl.*;
import io.factorialsystems.msscprovider.wsdl.mtn.Vend;
import io.factorialsystems.msscprovider.wsdl.mtn.VendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MtnAirtimeRecharge implements Recharge, ParameterCheck {
    private final MtnProperties properties;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        Vend vend = new Vend();
        vend.setOrigMsisdn(properties.getOriginMsisdn());
        vend.setDestMsisdn(request.getTelephone());
        vend.setAmount(request.getServiceCost().toString());
        vend.setSequence(request.getSequence().toString());
        vend.setTarifftypeId(request.getProductId());
        vend.setServiceproviderId(request.getServiceCode());
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());

        VendResponse vendResponse = (VendResponse) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), vend);

        if(!vendResponse.getStatusId().equals("0") || !vendResponse.getResponseCode().equals("0"))
            return RechargeStatus.builder()
                .status(HttpStatus.BAD_GATEWAY)
                .message(vendResponse.getResponseMessage())
                .build();

        if(vendResponse.getSeqstatus()!=null || !vendResponse.getSeqstatus().isBlank())
            return RechargeStatus.builder()
                    .status(HttpStatus.ALREADY_REPORTED)
                    .message("Duplicate transaction detected..")
                    .body(Map.of("ref", vendResponse.getTxRefId(), "status", vendResponse.getStatusId()))
                    .build();

        return RechargeStatus.builder()
                .status(HttpStatus.OK)
                .message(vendResponse.getResponseMessage())
                .build();
    }

    private HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setCredentials(usernamePasswordCredentials());
        return httpComponentsMessageSender;
    }

    private UsernamePasswordCredentials usernamePasswordCredentials() {
        return new UsernamePasswordCredentials(properties.getUser(), properties.getPassword());
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return false;//todo
    }
}