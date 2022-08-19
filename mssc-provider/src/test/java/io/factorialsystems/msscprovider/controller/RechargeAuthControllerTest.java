package io.factorialsystems.msscprovider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.service.TokenResponseDto;
import io.factorialsystems.msscprovider.utils.K;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@CommonsLog
class RechargeAuthControllerTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Test
    @SneakyThrows
    void startRecharge() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        // final String accessToken = getUserToken(id);

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

//            k.when(K::getAccessToken).thenReturn(accessToken);
//            assertThat(K.getAccessToken()).isEqualTo(accessToken);
//            log.info(accessToken);
//
//            SingleRechargeRequestDto dto = new SingleRechargeRequestDto();
//            dto.setRecipient("1505001425");
//            dto.setServiceCode("SMILE-DATA");
//            dto.setProductId("508");
//            dto.setPaymentMode("wallet");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(accessToken);
//            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), headers);
//
//            ResponseEntity<SingleRechargeResponseDto> responseEntity =
//                    restTemplate.exchange ("http://localhost:8081/api/v1/auth-recharge", HttpMethod.POST, request, SingleRechargeResponseDto.class);
//
//            log.info(responseEntity.getBody());
        }
    }

    private String getUserToken(String userId) {
        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
    }

    @Test
    void getDataPlans() {


    }


    private String getRealmAdminToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "password");
        requestBody.add("password", realmPassword);
        requestBody.add("username", realmUser);
        requestBody.add("scope", "openid");

        // Get the Realm Administrator Token
        return getToken(requestBody);
    }

    private String getUserToken(String userId, String realmToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", realmToken);
        requestBody.add("requested_subject", userId);

        return getToken(requestBody);
    }

    private String getToken(MultiValueMap<String, String> requestBody) {
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