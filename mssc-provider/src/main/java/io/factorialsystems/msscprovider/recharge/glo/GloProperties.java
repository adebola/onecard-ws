package io.factorialsystems.msscprovider.recharge.glo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GloProperties {

    @Value("${mtn.user}")
    private String userId;

    @Value("${mtn.password}")
    private String password;

    @Value("${mtn.url}")
    private String url;

    @Value("${mtn.msisdn}")
    private String originMsisdn;

    @Value("${mtn.account}")
    private String accountId;

    @Value("${mtn.account_type}")
    private String accountIdType;
}
