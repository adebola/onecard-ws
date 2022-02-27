package io.factorialsystems.msscprovider.recharge.glo;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.wsdl.glo.RequestTopup;
import io.factorialsystems.msscprovider.wsdl.glo.RequestTopupResponse;
import io.factorialsystems.msscprovider.wsdl.mtn.Vend;
import io.factorialsystems.msscprovider.wsdl.mtn.VendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GloAirtimeRecharge implements Recharge {
    private final GloProperties properties;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        RequestTopup requestTopup = new RequestTopup();
        RequestTopup.Amount amount = new RequestTopup.Amount();
        amount.setCurrency("NG");
        amount.setValue(request.getServiceCost().toString());

        requestTopup.setAmount(amount);
        RequestTopup.Context context = new RequestTopup.Context();
//        context.setChannel();
//        context.setClientComment();
//        context.setClientId();
//        context.setClientReference();
        context.setPassword(properties.getPassword());
//        context.setClientRequestTimeout();

        RequestTopup.Context.InitiatorPrincipalId initiatorPrincipalId = new RequestTopup.Context.InitiatorPrincipalId();
        initiatorPrincipalId.setId("DIST1");
        initiatorPrincipalId.setType("RESELLERUSER)");
        initiatorPrincipalId.setUserId(properties.getUserId());
        context.setInitiatorPrincipalId(initiatorPrincipalId);

        requestTopup.setContext(context);
        requestTopup.setProductId(request.getProductId());

        RequestTopup.TopupPrincipalId topupPrincipalId = new RequestTopup.TopupPrincipalId();
        topupPrincipalId.setId(request.getTelephone());
        topupPrincipalId.setType("RESELLERUSER");
        topupPrincipalId.setUserId(properties.getUserId());
        requestTopup.setTopupPrincipalId(topupPrincipalId);

        RequestTopup.TopupAccountSpecifier topupAccountSpecifier = new RequestTopup.TopupAccountSpecifier();
        topupAccountSpecifier.setAccountId(properties.getAccountId());
        topupAccountSpecifier.setAccountTypeId(properties.getAccountIdType());
        requestTopup.setTopupAccountSpecifier(topupAccountSpecifier);

        RequestTopup.SenderPrincipalId senderPrincipalId = new RequestTopup.SenderPrincipalId();
        senderPrincipalId.setId(properties.getOriginMsisdn());
        senderPrincipalId.setType("RESELLERUSER");
        senderPrincipalId.setUserId(properties.getUserId());
        requestTopup.setSenderPrincipalId(senderPrincipalId);

        RequestTopup.SenderAccountSpecifier senderAccountSpecifier = new RequestTopup.SenderAccountSpecifier();
        senderAccountSpecifier.setAccountId(properties.getAccountId());
        senderAccountSpecifier.setAccountTypeId(properties.getAccountIdType());
        requestTopup.setSenderAccountSpecifier(senderAccountSpecifier);


        RequestTopupResponse requestTopupResponse = (RequestTopupResponse) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), requestTopup);

//        if(!vendResponse.getStatusId().equals("0") || !vendResponse.getResponseCode().equals("0"))
//            return RechargeStatus.builder()
//                .status(HttpStatus.BAD_GATEWAY)
//                .message(vendResponse.getResponseMessage())
//                .build();
//
//        if(vendResponse.getSeqstatus()!=null || !vendResponse.getSeqstatus().isBlank())
//            return RechargeStatus.builder()
//                    .status(HttpStatus.ALREADY_REPORTED)
//                    .message("Duplicate transaction detected..")
//                    .body(Map.of("ref", vendResponse.getTxRefId(), "status", vendResponse.getStatusId()))
//                    .build();
//
//        return RechargeStatus.builder()
//                .status(HttpStatus.OK)
//                .message(vendResponse.getResponseMessage())
//                .build();

        return null;
    }
}