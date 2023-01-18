package io.factorialsystems.msscreports.service;

import io.factorialsystems.msscreports.dto.PagedDto;
import io.factorialsystems.msscreports.dto.RechargeReportRequestDto;
import io.factorialsystems.msscreports.dto.ReportDto;
import io.factorialsystems.msscreports.utils.K;
import lombok.extern.apachecommons.CommonsLog;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@CommonsLog
@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    private final String client_id = "public-client";
    private final String realmPassword = "password";
    private final String realmUser = "realm-admin";
    private final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Test
    void findReports() {
        PagedDto<ReportDto> reports = reportService.findReports(1, 20);
        assertNotNull(reports);
        assert(reports.getTotalSize() > 0);
        log.info(reports.getList().get(0));
    }

    @Test
    void searchReports() {
//        PagedDto<ReportDto> reports = reportService.searchReports(1, 20, "Or");
//        assertNotNull(reports);
//        assert(reports.getTotalSize() > 0);
//        log.info(reports.getList().get(0));
    }

    @Test
    void findReportById() {
        ReportDto dto = reportService.findReportById(1);
        assertNotNull(dto);
        log.info(dto);
    }

//    @Test
//    void saveReport() {
//        ReportDto dto = new ReportDto();
//        dto.setReportFile("onecard.jrxml");
//        dto.setReportName("Order Reports");
//        dto.setReportDescription("Jesus Christ is the Son of God");
//
//        Integer reportId = reportService.saveReport("adebola", dto);
//        ReportDto newReport = reportService.findReportById(reportId);
//
//        assertNotNull(newReport);
//        assertEquals(newReport.getReportFile(), dto.getReportFile());
//        assertEquals(newReport.getReportName(), dto.getReportName());
//        assertEquals(newReport.getReportDescription(), dto.getReportDescription());
//    }

//    @Test
//    void updateReport() {
//
//        String s = "Order Report Updated";
//
//        ReportDto dto = reportService.findReportById(1);
//        dto.setReportName(s);
//        reportService.updateReport(1, dto);
//
//        ReportDto newReport = reportService.findReportById(1);
//        assertNotNull(newReport);
//        assertEquals(newReport.getReportName(), dto.getReportName());
//    }

//    @Test
//    void runReport() {
//        ByteArrayInputStream in = reportService.runReport(1);
//        assertNotNull(in);
//        log.info(in.toString());
//    }

    @Test
    void runRechargeReport() throws IOException {
        RechargeReportRequestDto dto = new RechargeReportRequestDto();

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String accessToken = getUserToken(id);

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);

            InputStreamResource inputStreamResource = reportService.runRechargeReport(dto);

            File targetFile = new File("recharge-report.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = inputStreamResource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
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
