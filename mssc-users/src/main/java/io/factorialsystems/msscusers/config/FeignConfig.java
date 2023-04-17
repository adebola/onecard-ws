package io.factorialsystems.msscusers.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import io.factorialsystems.msscusers.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class FeignConfig {
    private static final String TOKEN_TYPE = "Bearer";
    private static final String AUTHORIZATION_HEADER="Authorization";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final String token = K.getAccessToken();
            if (token != null) {
                requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", TOKEN_TYPE, token));
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder () {
        return new FeignCustomErrorDecoder();
    }

    @Bean
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(() -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
    }
}
