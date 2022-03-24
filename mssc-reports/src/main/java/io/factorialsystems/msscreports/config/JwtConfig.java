package io.factorialsystems.msscreports.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Configuration
public class JwtConfig {

    @Bean
    public TokenStore tokenStore() {
        log.info("TOKENSTORE");
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        log.info("ACCESSTOKENCONVERTER");
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        converter.setJwtClaimsSetVerifier(issuerClaimVerifier());
        return converter;
    }

    @Bean
    public JwtClaimsSetVerifier issuerClaimVerifier() {
        log.info("New IssueClaimVerifier");

        try {
            return new IssuerClaimVerifier(new URL("http://localhost:8082"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
