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

    public static String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> claims = jwt.getClaims();

            return (String) claims.get("name");
        }

        return SYSTEM_NAME;
    }

    public static String getPreferredUserName() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> claims = jwt.getClaims();

            return (String) claims.get("preferred_username");
        }
        return SYSTEM_PREFERRED_NAME;
    }

    public static String getAccessToken() {
//        String secret = "ca8f3fe3-b0da-4528-b0c1-0dae59e5015b";
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }
}
