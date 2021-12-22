package io.factorialsystems.msscprovider.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class K {
    private static final String SYSTEM_NAME = "__debug";
    private static final String SYSTEM_EMAIL = "anonymous@factorialsystems.io";
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final String SERVICE_STATUS = "Provider Service running on Port :";

    public static String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> claims = jwt.getClaims();

            return (String) claims.get("name");
        }

        return SYSTEM_NAME;
    }

    public static String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> claims = jwt.getClaims();

            return (String) claims.get("email");
        }

        return SYSTEM_EMAIL;
    }
}
