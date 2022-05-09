package io.factorialsystems.msscusers.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class K {
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 25;
    public static final String SERVICE_STATUS = "User Service running on Port :";
    private static final String SYSTEM_NAME = "_debug";
    private static final String SYSTEM_PREFERRED_NAME = "adeomoboya@googlemail.com";

    public static final int ACCOUNT_TYPE_PERSONAL = 1;
    public static final int ACCOUNT_TYPE_CORPORATE = 2;

    public static final String ROLES_ONECARD = "Onecard";
    public static final String ROLES_COMPANY = "Company";
    public static final String ROLES_ONECARD_ADMIN = "ROLE_Onecard_Admin";
    public static final String ROLES_COMPANY_ADMIN = "ROLE_Company_Admin";

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        }

        return null;
    }

    public static List<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        return null;
    }

    public static Boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream().anyMatch(s -> s.getAuthority().equals(K.ROLES_ONECARD_ADMIN));
    }

    public static Boolean isCompanyAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream().anyMatch(s -> s.getAuthority().equals(K.ROLES_COMPANY_ADMIN));
    }
}
