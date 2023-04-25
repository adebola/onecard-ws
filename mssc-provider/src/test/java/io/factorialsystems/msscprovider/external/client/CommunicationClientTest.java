package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.dto.TokenResponseDto;
import io.factorialsystems.msscprovider.service.MailService;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootTest
@CommonsLog
class CommunicationClientTest {

    @Autowired
    private CommunicationClient client;

    @Autowired
    private MailService mailService;

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";



    @Test
    void sendMailWithoutAttachment() {
//        MailMessageDto dto = MailMessageDto.builder()
//                .body("Test Mail without Attachment")
//                .to("adeomoboya@gmail.com")
//                .subject("Test Mail")
//                .build();
//
//        String s = mailService.sendMailWithOutAttachment(dto);
//        log.info(s);
    }

    @Test
    void sendMailWithAttachment() throws IOException {
//        File file = new File("/Users/adebola/Downloads/TOPUP.xlsx");
//
//        if (file.exists()) {
//            MailMessageDto dto = MailMessageDto.builder()
//                    .body("Test Mail with Attachment")
//                    .fileName("connect.txt")
//                    .to("adeomoboya@gmail.com")
//                    .subject("Test Mail")
//                    .sentBy("Adebola Omoboya")
//                    .build();
//            log.info(String.format("File exists name %s", file.getName()));
//            String s = mailService.sendMailWithAttachment(file, dto, "file", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            log.info(s);
//
//        }
    }

    @Test
    public void uploadFile() throws IOException {
//        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
//        final String accessToken = getUserToken(id);
//
//        try (MockedStatic<ProviderSecurity> security  = Mockito.mockStatic(ProviderSecurity.class)) {
//            security.when(ProviderSecurity::getUserId).thenReturn(id);
//            assert Objects.equals(ProviderSecurity.getUserId(), id);
//
//            security.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
//            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(accessToken);
//            log.info(ProviderSecurity.getAccessToken());
//            File file = new File("/Users/adebola/Downloads/TOPUP.xlsx");
//            FileInputStream input = new FileInputStream(file);
//
//            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                    IOUtils.toByteArray(input));
//
//            String s = client.uploadFile(multipartFile);
//            log.info(s);
//        }
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