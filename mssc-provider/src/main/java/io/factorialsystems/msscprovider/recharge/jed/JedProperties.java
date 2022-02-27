package io.factorialsystems.msscprovider.recharge.jed;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JedProperties {

    @Value("${jed.recharge.url}")
    private String url;

    @Value("${jed.token}")
    private String token;

    @Value("${jed.privatekey}")
    private String privateKey;
}