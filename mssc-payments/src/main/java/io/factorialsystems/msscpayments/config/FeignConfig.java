package io.factorialsystems.msscpayments.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import io.factorialsystems.msscpayments.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscpayments.utils.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final String token = Security.getAccessToken();
            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + Security.getAccessToken());
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
