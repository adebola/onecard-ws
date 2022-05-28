package io.factorialsystems.msscprovider.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@Slf4j
public class K {
    private static final String SYSTEM_NAME = "anonymous";
    private static final String SYSTEM_EMAIL = "anonymous@factorialsystems.io";
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final String PAYSTACK_PAY_MODE = "paystack";
    public static final String WALLET_PAY_MODE = "wallet";
    public static final String BULK_RECHARGE = "bulk";
    public static final String SINGLE_RECHARGE = "single";
    public static final String[] ALL_PAYMENT_MODES = {PAYSTACK_PAY_MODE, WALLET_PAY_MODE };
    public static final String HEADER_EMAIL = "email";
    public static final String HEADER_PASSWORD = "password";

    private static Map<String, Object> getClaims () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? ((Jwt)authentication.getPrincipal()).getClaims() : null;
    }

    public static String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? ((Jwt)authentication.getPrincipal()).getTokenValue() : null;
    }

    public static String getUserName() {
        Map<String, Object> claims = K.getClaims();
        return claims != null ? (String) claims.get("name") : SYSTEM_NAME;
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
