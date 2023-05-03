package io.factorialsystems.msscreports.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.factorialsystems.msscreports.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscreports.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final String token = K.getAccessToken();
            if (token == null) return;
            requestTemplate.header("Authorization", "Bearer " + K.getAccessToken());
        };
    }

    @Bean
    public ErrorDecoder errorDecoder () {
        return new FeignCustomErrorDecoder();
    }
}
