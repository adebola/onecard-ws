package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@CommonsLog
@SpringBootTest
class RechargeReportServiceTest {
    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private RechargeReportMapper mapper;

    @Autowired
    private RechargeReportService service;

    @Test
    void getShortRechargeTotals() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
        String dateInString = "22-01-2015 10:15:55 AM";
        Date date = formatter.parse(dateInString);

        RechargeProviderRequestDto dto = new RechargeProviderRequestDto();
        dto.setStartDate(date);

        var x = mapper.findRechargeProviderExpenditure(dto);
        assertNotNull(x);
        log.info(x);
    }

    @Test
    void runRechargeReport_All() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);

        try (MockedStatic<ProviderSecurity> security  = Mockito.mockStatic(ProviderSecurity.class)) {
            security.when(ProviderSecurity::getUserId).thenReturn(id);
            assert Objects.equals(ProviderSecurity.getUserId(), id);

            security.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
            assert Objects.equals(ProviderSecurity.getAccessToken(), accessToken);
            log.info(ProviderSecurity.getAccessToken());

            RechargeReportRequestDto dto = new RechargeReportRequestDto();
            CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
            assertNotNull(combinedRechargeRequests);

            log.info(combinedRechargeRequests.getRequests().size());
            log.info(combinedRechargeRequests);
        }
    }

    @Test
    void runRechargeReport_Single() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);

        try (MockedStatic<ProviderSecurity> security  = Mockito.mockStatic(ProviderSecurity.class)) {
            security.when(ProviderSecurity::getUserId).thenReturn(id);
            assert Objects.equals(ProviderSecurity.getUserId(), id);

            security.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
            assert Objects.equals(ProviderSecurity.getAccessToken(), accessToken);
            log.info(ProviderSecurity.getAccessToken());

            RechargeReportRequestDto dto = new RechargeReportRequestDto();
            dto.setType("single");
            CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
            assertNotNull(combinedRechargeRequests);

            log.info(combinedRechargeRequests.getRequests().size());
            log.info(combinedRechargeRequests);
        }
    }

    @Test
    void runRechargeReport_Bulk() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);

        try (MockedStatic<ProviderSecurity> security  = Mockito.mockStatic(ProviderSecurity.class)) {
            security.when(ProviderSecurity::getUserId).thenReturn(id);
            assert Objects.equals(ProviderSecurity.getUserId(), id);

            security.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
            assert Objects.equals(ProviderSecurity.getAccessToken(), accessToken);
            log.info(ProviderSecurity.getAccessToken());

            RechargeReportRequestDto dto = new RechargeReportRequestDto();
            dto.setType("bulk");

            CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
            assertNotNull(combinedRechargeRequests);


            log.info(combinedRechargeRequests.getRequests().size());
            log.info(combinedRechargeRequests);
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