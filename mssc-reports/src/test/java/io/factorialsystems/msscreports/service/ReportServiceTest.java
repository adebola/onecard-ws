package io.factorialsystems.msscreports.service;

import io.factorialsystems.msscreports.dto.*;
import io.factorialsystems.msscreports.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@CommonsLog
@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    private final static String client_id = "public-client";
    private final static String realmPassword = "password";
    private final static String realmUser = "realm-admin";
    private final static String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    private static String token = null;
    private static final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";

    @BeforeAll
    static void setUp() {
        token = getUserToken(id);
    }

    @Test
    void findReports() {
        PagedDto<ReportDto> reports = reportService.findReports(1, 20);
        assertNotNull(reports);
        assert(reports.getTotalSize() > 0);
        log.info(reports.getList().get(0));
    }

    @Test
    void findReportById() {
        ReportDto dto = reportService.findReportById(1);
        assertNotNull(dto);
        log.info(dto);
    }

    @Test
    void runRechargeReport() throws IOException {
        RechargeReportRequestDto dto = new RechargeReportRequestDto();

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runRechargeReport(dto);

            File targetFile = new File("/Users/adebola/Downloads/recharge-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void runWalletReport_User() throws IOException {
        WalletReportRequestDto dto = new WalletReportRequestDto();
        dto.setType("user");

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runWalletReport(dto);

            File targetFile = new File("/Users/adebola/Downloads/wallet-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void runWalletReport_ProviderShort() throws IOException {
        WalletReportRequestDto dto = new WalletReportRequestDto();
        dto.setType("provider-short");

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runWalletReport(dto);

            File targetFile = new File("/Users/adebola/Downloads/short-provider-wallet-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void runWalletReport_ProviderLong() throws IOException {
        WalletReportRequestDto dto = new WalletReportRequestDto();
        dto.setType("provider-long");

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runWalletReport(dto);

            File targetFile = new File("/Users/adebola/Downloads/long-provider-wallet-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

//    @Test
//    void runProviderBalanceReport() throws IOException {
//        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
//            k.when(K::getUserId).thenReturn(id);
//            assertThat(K.getUserId()).isEqualTo(id);
//            log.info(K.getUserId());
//
//            k.when(K::getAccessToken).thenReturn(token);
//            assertThat(K.getAccessToken()).isEqualTo(token);
//
//            InputStreamResource inputStreamResource = reportService.runProviderWalletBalanceReport();
//
//            File targetFile = new File("/Users/adebola/Downloads/provider-balance-report.xlsx");
//            OutputStream outputStream = new FileOutputStream(targetFile);
//            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
//            outputStream.write(buffer);
//
//            log.info(targetFile.getAbsolutePath());
//        }
//    }


    @Test
    void runAuditReport() throws IOException {

        //01-07-2023 00:00:00
        //String start = "2022-02-15 18:35:24";
        String start = "2023-07-01 00:00:00";
        String end = "2022-11-15 18:35:24";
        AuditSearchDto auditSearchDto = new AuditSearchDto(null, start, null);

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runAuditReport(auditSearchDto);

            File targetFile = new File("/Users/adebola/Downloads/audit-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void runUserReport() throws IOException {
        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(token);
            assertThat(K.getAccessToken()).isEqualTo(token);

            InputStreamResource inputStreamResource = reportService.runUserReport();


            File targetFile = new File("/Users/adebola/Downloads/user-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }

    }

    private static String getUserToken(String userId) {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
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
