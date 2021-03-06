package io.factorialsystems.msscprovider.recharge.ringo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RingoProperties {
    @Value("${ringo.mail}")
    private String mail;

    @Value("${ringo.password}")
    private String password;

    @Value("${ringo.airtime.servicecode}")
    private String airtimeServiceCode;

    @Value("${ringo.data.servicecode}")
    private String dataServiceCode;

    @Value("${ringo.electric.servicecode}")
    private String electricServiceCode;

    @Value("${ringo.electric.verify.servicecode}")
    private String electricVerifyServiceCode;

    @Value("${ringo.airtime.url}")
    private String airtimeUrl;

    @Value("${ringo.other.data.servicecode}")
    private String otherDataServiceCode;

    @Value("${ringo.enquiry.data.servicecode}")
    private String enquiryDataCode;

    @Value("${ringo.spectranet.type}")
    private String spectranetType;

    @Value("${ringo.smile.type}")
    private String smileType;

    @Value("${ringo.cable.service.type.dstv}")
    private String dstv;

    @Value("${ringo.cable.service.type.gotv}")
    private String gotv;

    @Value("${ringo.cable.service.type.startimes}")
    private String startimes;

    @Value("${ringo.cable.service.code.verification}")
    private String cableVerification;

    @Value("${ringo.cable.service.code.payment}")
    private String cablePayment;
}
