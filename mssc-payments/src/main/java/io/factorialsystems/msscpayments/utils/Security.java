package io.factorialsystems.msscpayments.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class Security {
    private static final String SYSTEM_NAME = "__debug";
    private static final String SYSTEM_EMAIL = "anonymous@factorialsystems.io";

    // Start
    private static Map<String, Object> getClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? ((Jwt) authentication.getPrincipal()).getClaims() : null;
    }

    public static String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? ((Jwt) authentication.getPrincipal()).getTokenValue() : null;
    }

    public static String getUserName() {
        Map<String, Object> claims = getClaims();
        return claims != null ? (String) claims.get("name") : SYSTEM_NAME;
    }

    public static String getUserId() {
        Map<String, Object> claims = getClaims();
        return claims != null ? (String) claims.get("sub") : null;
    }

    public static String getEmail() {
        Map<String, Object> claims = getClaims();
        return claims != null ? (String) claims.get("email") : SYSTEM_EMAIL;
    }
    // End

//    public static String getAccessToken() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            Jwt jwt = (Jwt)authentication.getPrincipal();
//            return jwt.getTokenValue();
//        }
//
//        return null;
//    }
//    public static String getUserName() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null) {
//            Jwt jwt = (Jwt) authentication.getPrincipal();
//            Map<String, Object> claims = jwt.getClaims();
//
//            return (String) claims.get("name");
//        }
//
//        return SYSTEM_NAME;
//    }
//
//    public static String getEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null) {
//            Jwt jwt = (Jwt) authentication.getPrincipal();
//            Map<String, Object> claims = jwt.getClaims();
//
//            return (String) claims.get("email");
//        }
//
//        return SYSTEM_EMAIL;
//    }
}
