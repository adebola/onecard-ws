package io.factorialsystems.msscreports.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.Map;

@Slf4j
public class NewIssuerClaimVerifier implements JwtClaimsSetVerifier {
    private final URL issuer;
    private static final String ISS_CLAIM = "iss";

    public NewIssuerClaimVerifier(URL issuer) {
        this.issuer = issuer;
    }

    @Override
    public void verify(Map<String, Object> claims) throws InvalidTokenException {
        log.info("11111112222222223333344444444455555");

        if (!CollectionUtils.isEmpty(claims) && claims.containsKey("iss")) {
            String jwtIssuer = (String)claims.get("iss");
            log.info("Issuer {}, Issuer {}", jwtIssuer, issuer);
            if (!jwtIssuer.equals(this.issuer.toString())) {
                throw new InvalidTokenException("Invalid Issuer (iss) claim: " + jwtIssuer);
            }
        }
    }
}
