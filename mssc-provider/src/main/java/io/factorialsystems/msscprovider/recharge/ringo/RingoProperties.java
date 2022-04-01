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

    @Value("${ringo.airtime.url}")
    private String airtimeUrl;

    @Value("${ringo.base.url}")
    private String baseUrl;

    @Value("${ringo.waec.type}")
    private String waecType;

    @Value("${ringo.waec.validate.code}")
    private String waecServiceCodeValidate;

    @Value("${ringo.waec.buy.code}")
    private String waecServiceCodeBuy;
}