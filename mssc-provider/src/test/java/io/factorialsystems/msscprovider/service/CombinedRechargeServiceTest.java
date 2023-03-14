package io.factorialsystems.msscprovider.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;

@CommonsLog
@SpringBootTest
class CombinedRechargeServiceTest {

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private CombinedRechargeService combinedRechargeService;

    @Test
    public void getCombinedResource() throws ParseException, IOException {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);

//        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(ProviderSecurity::getUserId).thenReturn(id);
//            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
//            log.info(ProviderSecurity.getUserId());
//
//            k.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
//            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(accessToken);
//            log.info(ProviderSecurity.getAccessToken());
//
//            CombinedRequestDto dto = new CombinedRequestDto();
//            dto.setId("e33b6988-e636-44d8-894d-c03c982d8fa5");
//
//            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
//            formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
//
//            String startString = "22-01-2015 10:15:55 AM";
//            String endString = "22-08-2022 10:15:55 AM";
//
//            Date startDate = formatter.parse(startString);
//            Date endDate = formatter.parse((endString));
//
//            dto.setStartDate(startDate);
//            dto.setEndDate(null);
//
////            InputStreamResource resource = new InputStreamResource(combinedRechargeService.getCombinedResource(dto));
////            File targetFile = new File("test4.xlsx");
////            OutputStream outputStream = new FileOutputStream(targetFile);
////            byte[] buffer = resource.getInputStream().readAllBytes();
////            outputStream.write(buffer);
////
////            log.info(targetFile.getAbsolutePath());
//        }
    }

    private String getUserToken(String userId) {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
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