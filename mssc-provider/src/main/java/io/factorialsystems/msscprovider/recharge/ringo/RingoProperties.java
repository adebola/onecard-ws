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

    @Value("${ringo.airtime.requestid}")
    private String airtimeRequestId;

    @Value("${ringo.airtime.servicecode}")
    private String airtimeServiceCode;

    @Value("${ringo.data.requestid}")
    private String dataRequestId;

    @Value("${ringo.data.servicecode}")
    private String dataServiceCode;

    @Value("${ringo.airtime.url}")
    private String airtimeUrl;
}
