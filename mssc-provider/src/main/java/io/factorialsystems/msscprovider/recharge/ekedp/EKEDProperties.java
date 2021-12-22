package io.factorialsystems.msscprovider.recharge.ekedp;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EKEDProperties {

    @Value("${ekedp.email}")
    private String mail;

    @Value("${ekedp.partnerid}")
    private String partnerId;

    @Value("${ekedp.accesskey}")
    private String accessKey;

    @Value("${ekedp.url}")
    private String url;
}
