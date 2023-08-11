package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dao.SMSVerificationMapper;
import io.factorialsystems.msscwallet.dto.SMSVerificationRequestDto;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@CommonsLog
@SpringBootTest
class KycServiceTest {

    @Autowired
    KycService kycService;

    @Autowired
    SMSVerificationMapper smsVerificationMapper;

    final static String client_id = "public-client";
    final static String realmPassword = "password";
    final static String realmUser = "realm-admin";
    final static String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    private static String token = null;
    private static final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";

    @BeforeAll
    static void setUp() {
        token = getUserToken();
    }

    @Test
    void startSMSVerification() {
        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            k.when(Security::getAccessToken).thenReturn(token);
            assertThat(Security.getAccessToken()).isEqualTo(token);
            log.info(Security.getAccessToken());

            final Map<String, String> map = kycService.startSMSVerification("08055572307");
            log.info(map);
        }
    }

    @Test
    void finalizeSMSVerification_expired() {
        final String verificationId = "dd08ac9e-9268-4bf3-8910-a00b4dcc2518";

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            k.when(Security::getAccessToken).thenReturn(token);
            assertThat(Security.getAccessToken()).isEqualTo(token);
            log.info(Security.getAccessToken());

            SMSVerificationRequestDto requestDto = new SMSVerificationRequestDto();
            requestDto.setCode("00976");
            requestDto.setId(verificationId);

            final Map<String, String> map = kycService.finalizeSMSVerification(requestDto);
            log.info(map);
        }
    }

    @Test
    void bvnVerification() {
        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            k.when(Security::getAccessToken).thenReturn(token);
            assertThat(Security.getAccessToken()).isEqualTo(token);
            log.info(Security.getAccessToken());

            k.when(Security::getUserName).thenReturn("user");

            final Map<String, String> map = kycService.bvnVerification("12345678901");
            log.info(map);
        }
    }

    private static String getUserToken() {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(KycServiceTest.id, realmToken);
    }

    private static String getRealmAdminToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "password");
        requestBody.add("password", realmPassword);
        requestBody.add("username", realmUser);
        requestBody.add("scope", "openid");

        // Get the Realm Administrator Token
        return getToken(requestBody);
    }

    private static String getUserToken(String userId, String realmToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", realmToken);
        requestBody.add("requested_subject", userId);

        return getToken(requestBody);
    }

    private static String getToken(MultiValueMap<String, String> requestBody) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponseDto> response =
                restTemplate.exchange(authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        return token.getAccess_token();
    }
}