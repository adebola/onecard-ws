package io.factorialsystems.msscapiuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate getTemplate() {
        return new RestTemplate();
    }
}
