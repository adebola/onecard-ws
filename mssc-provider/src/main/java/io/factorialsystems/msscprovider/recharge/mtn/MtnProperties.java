package io.factorialsystems.msscprovider.recharge.mtn;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MtnProperties {

    @Value("${mtn.user}")
    private String user;

    @Value("${mtn.password}")
    private String password;

    @Value("${mtn.url}")
    private String url;

    @Value("${mtn.msisdn}")
    private String originMsisdn;
}
