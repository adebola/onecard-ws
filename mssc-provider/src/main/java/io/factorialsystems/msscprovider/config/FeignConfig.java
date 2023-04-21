package io.factorialsystems.msscprovider.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.factorialsystems.msscprovider.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final String token = ProviderSecurity.getAccessToken();
            if (token == null) return;
            requestTemplate.header("Authorization", "Bearer " + ProviderSecurity.getAccessToken());
        };
    }

    @Bean
    public ErrorDecoder errorDecoder () {
        return new FeignCustomErrorDecoder();
    }
}
