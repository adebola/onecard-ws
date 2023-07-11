package io.factorialsystems.msscprovider.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {
    public static final String DSTV_GOTV_PLAN_CACHE = "dstv-gotv-plans";
    public static final String PARAMETER_CACHE = "parameters";
    public static final String RINGO_MOBILE_DATA_PLAN_CACHE = "ringodataplans";
    public static final String SMILE_DATA_PLAN_CACHE = "smiledataplans";
    public static final String RINGO_SMILE_DATA_PLAN_CACHE = "ringosmiledataplans";
    public static final String SPECTRANET_DATA_PLAN = "spectranetdataplans";
    public static final String ALTERNATE_USER_ID = "alternateuser";
    public static final String ONECARD_PLAN_CACHE = "onecardplans";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager (DSTV_GOTV_PLAN_CACHE,
                PARAMETER_CACHE,
                RINGO_MOBILE_DATA_PLAN_CACHE,
                SMILE_DATA_PLAN_CACHE,
                RINGO_SMILE_DATA_PLAN_CACHE,
                SPECTRANET_DATA_PLAN,
                ALTERNATE_USER_ID,
                ONECARD_PLAN_CACHE
        );
    }
}
