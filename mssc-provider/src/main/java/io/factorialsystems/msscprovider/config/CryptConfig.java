package io.factorialsystems.msscprovider.config;

import io.factorialsystems.msscprovider.recharge.onecard.MCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptConfig {
    @Bean
    MCrypt mCrypt(@Value("${onecard.api.key}") String key, @Value("${onecard.api.salt}") String salt) {
        return new MCrypt(key, salt);
    }
}
