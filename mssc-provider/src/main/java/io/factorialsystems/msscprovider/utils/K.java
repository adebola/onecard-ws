package io.factorialsystems.msscprovider.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class K {
    private static final String SYSTEM_NAME = "anonymous";
    private static final String SYSTEM_EMAIL = "anonymous@factorialsystems.io";
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final String SERVICE_STATUS = "Provider Service running on Port :";
    public static final String PAYSTACK_PAY_MODE = "paystack";
    public static final String WALLET_PAY_MODE = "wallet";
    public static final String BULK_RECHARGE = "bulk";
    public static final String SINGLE_RECHARGE = "single";
    public static final String[] ALL_PAYMENT_MODES = {PAYSTACK_PAY_MODE, WALLET_PAY_MODE };
    public static final String[] ALL_RECHARGE_MODES = { SINGLE_RECHARGE, BULK_RECHARGE};

    private static Map<String, Object> getClaims () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaims();
        }

        return null;
    }

    public static String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt)authentication.getPrincipal();
            return jwt.getTokenValue();
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

    public static String getUserId() {
        Map<String, Object> claims = K.getClaims();

        if (claims != null) {
            return (String) claims.get("sub");
        }

        return null;
    }

    public static String getEmail() {
        Map<String, Object> claims = K.getClaims();

        if (claims != null) {
            return (String) claims.get("email");
        }

        return SYSTEM_EMAIL;
    }
}
