package io.factorialsystems.mssccommunication.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@Slf4j
public class K {
    private static final String SYSTEM_NAME = "system";
    private static final String SYSTEM_EMAIL = "system@onecardnigeria.com";

    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    private static Map<String, Object> getClaims () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? ((Jwt)authentication.getPrincipal()).getClaims() : null;
    }

    public static String getUserId() {
        Map<String, Object> claims = K.getClaims();
        return claims != null ? (String) claims.get("sub") : null;
    }

    public static String getEmail() {
        Map<String, Object> claims = K.getClaims();
        return claims != null ? (String) claims.get("email") : SYSTEM_EMAIL;
    }

}
