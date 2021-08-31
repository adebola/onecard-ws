package io.factorialsystems.msscprovider.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class K {
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 25;
    public static final String SERVICE_STATUS = "Provider Service running on Port :";

    public static String getUserName() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        return (String) claims.get("name");
    }
}
