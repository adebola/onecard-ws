package io.factorialsystems.msscprovider.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GeneralProperties {
    @Value("${api.local.host.baseurl}")
    private String baseUrl;
}
