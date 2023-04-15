package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.dto.PaymentRequestDto;
import io.factorialsystems.msscwallet.service.TokenResponseDto;
import io.factorialsystems.msscwallet.utils.Security;
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

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@CommonsLog
class PaymentClientTest {
    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private PaymentClient paymentClient;

    @Test
    public void testNoAuthPayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .redirectUrl("redirectUrl")
                .paymentMode("paystack")
                .build();

        PaymentRequestDto paymentRequestDto = paymentClient.initializePayment(dto);
        log.info(paymentRequestDto);
    }

    @Test
    public void checkPayment() {
        final String id = "002c2023-83e8-44a8-89e8-31f7c2357449";
        PaymentRequestDto paymentRequestDto = paymentClient.checkPayment(id);
        log.info(paymentRequestDto);
    }

    @Test
    public void testAuthPayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1000))
                .redirectUrl("redirectUrl")
                .paymentMode("wallet")
                .build();

        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);

        try (MockedStatic<Security> security  = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);

            security.when(Security::getAccessToken).thenReturn(accessToken);
            assertThat(Security.getAccessToken()).isEqualTo(accessToken);
            log.info(Security.getAccessToken());

            //PaymentRequestDto paymentRequestDto = paymentClient.makePayment(dto);
            //log.info(paymentRequestDto);
        }
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

    private String getUserToken(String userId) {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
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
                restTemplate.exchange (authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        return  token.getAccess_token();
    }
}