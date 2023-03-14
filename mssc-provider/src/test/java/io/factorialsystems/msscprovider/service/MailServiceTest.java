package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@CommonsLog
class MailServiceTest {
    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private MailService service;

    @Autowired
    private NewBulkRechargeService bulkRechargeService;

    @Test
    @WithMockUser
    void sendMailWithOutAttachment() {

        //final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        //final String accessToken = getUserToken(id);

//        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(Constants::getUserId).thenReturn(id);
//            k.when(Constants::getAccessToken).thenReturn(accessToken);
//
//            assertThat(Constants.getUserId()).isEqualTo(id);
//            assertThat(Constants.getAccessToken()).isEqualTo(accessToken);
//            log.info(Constants.getUserId());
//            log.info(Constants.getAccessToken());
//
//            MailMessageDto dto = MailMessageDto.builder()
//                    .body("Test Message Jesus Is Lord")
//                    .to("adeomoboya@gmail.com")
//                    .subject("Test Subject")
//                    .build();
//
//            var x = service.sendMailWithOutAttachment(dto);
//            log.info(x);
//        }
    }

    @Test
    void sendMailWithAttachment() throws Exception {
        final String bulkId = "268c0450-172c-4cbd-aad5-9368ace533a6";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);

//        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(Constants::getUserId).thenReturn(id);
//            k.when(Constants::getAccessToken).thenReturn(accessToken);
//
//            assertThat(Constants.getUserId()).isEqualTo(id);
//            assertThat(Constants.getAccessToken()).isEqualTo(accessToken);
//            log.info(Constants.getUserId());
//            log.info(Constants.getAccessToken());
//
//            MailMessageDto dto = MailMessageDto.builder()
//                    .body("Test Message Jesus Is Lord")
//                    .to("adeomoboya@yahoo.co.uk")
//                    .subject("Test Subject With Attachment")
//                    .build();
//
//            InputStreamResource resource = new InputStreamResource(bulkRechargeService.generateExcelFile(bulkId));
//            File targetFile = new File("test2.xlsx");
//            OutputStream outputStream = new FileOutputStream(targetFile);
//            byte[] buffer = resource.getInputStream().readAllBytes();
//            outputStream.write(buffer);
//
//            FileSystemResource fileSystemResource = new FileSystemResource(targetFile);
//
//            var x = service.sendMailWithAttachment(fileSystemResource, dto);
//            log.info(x);
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
                restTemplate.exchange (authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        return  token.getAccess_token();
    }
}