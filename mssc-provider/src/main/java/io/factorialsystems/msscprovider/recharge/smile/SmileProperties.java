package io.factorialsystems.msscprovider.recharge.smile;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SmileProperties {
    @Value("${smile.username}")
    private String userName;

    @Value("${smile.password}")
    private String pasword;

    @Value("${smile.source-account}")
    private String sourceAccount;

    @Value("${smile.url}")
    private String url;
}
