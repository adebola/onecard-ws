package io.factorialsystems.msscwallet.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class Security {
    private static final String SYSTEM_NAME = "anonymous";
    private static final String SYSTEM_EMAIL = "anonymous@factorialsystems.io";

    public static String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Jwt jwt = (Jwt)authentication.getPrincipal();
            return jwt.getTokenValue();
        }

        return null;
    }

    private static Map<String, Object> getClaims () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaims();
        }

        return null;
    }

    public static String getUserName() {
        Map<String, Object> claims = Security.getClaims();

        if (claims != null) {
            return (String) claims.get("name");
        }

        return SYSTEM_NAME;
    }

    public static String getUserId() {
        Map<String, Object> claims = Security.getClaims();

        if (claims != null) {
            return (String) claims.get("sub");
        }

        return null;
    }

    public static String getEmail() {
        Map<String, Object> claims = Security.getClaims();
        return claims != null ? (String) claims.get("email") : SYSTEM_EMAIL;
    }
}
