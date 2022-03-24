package io.factorialsystems.msscusers.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class K {
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 25;
    public static final String SERVICE_STATUS = "User Service running on Port :";
    private static final String SYSTEM_NAME = "_debug";
    private static final String SYSTEM_PREFERRED_NAME = "adeomoboya@googlemail.com";

    public static final int ACCOUNT_TYPE_PERSONAL = 1;
    public static final int ACCOUNT_TYPE_CORPORATE = 2;

    private static Map<String, Object> getClaims () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaims();
        }

        return null;
    }

    public static String getUserName() {
        Map<String, Object> claims = K.getClaims();

        if (claims != null) {
            return (String) claims.get("name");
        }

        return SYSTEM_NAME;
    }

    public static String getPreferredUserName() {

        Map<String, Object> claims = K.getClaims();

        if (claims != null) {
            return (String) claims.get("preferred_username");
        }

        return SYSTEM_PREFERRED_NAME;
    }

    public static String getUserId() {
        Map<String, Object> claims = K.getClaims();

        if (claims != null) {
            return (String) claims.get("sub");
        }

        return null;
    }

    public static String getAccessToken() {
//        String secret = "ca8f3fe3-b0da-4528-b0c1-0dae59e5015b";
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }
}
