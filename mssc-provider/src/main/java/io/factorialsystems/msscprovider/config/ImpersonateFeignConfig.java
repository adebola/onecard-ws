package io.factorialsystems.msscprovider.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.factorialsystems.msscprovider.external.decoder.FeignCustomErrorDecoder;
import io.factorialsystems.msscprovider.security.Keycloak;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

@Slf4j
public class ImpersonateFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(Keycloak keycloak, CacheManager cacheManager) {
        return requestTemplate -> {
            String token = null;
            Cache cache = cacheManager.getCache(CachingConfig.ALTERNATE_USER_ID);

            if (cache != null) {
                Cache.ValueWrapper valueWrapper = cache.get("user");

                if (valueWrapper != null) {
                    token = keycloak.getUserToken(String.valueOf(valueWrapper.get()));
                }
            }

            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder () {
        return new FeignCustomErrorDecoder();
    }
}
