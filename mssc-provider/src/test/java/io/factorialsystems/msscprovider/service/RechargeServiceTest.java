package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.service.singlerecharge.SingleRechargeService;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRefundRecharge;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CommonsLog
class RechargeServiceTest {
    @Autowired
    private SingleRechargeService rechargeService;

    @Autowired
    private SingleRechargeMapper singleRechargeMapper;

    @Autowired
    private SingleRefundRecharge singleRefundRecharge;

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Test
    void testCloseRequest() {
        Map<String, String> resultsMap = new HashMap<>();
        resultsMap.put("id", "04c462eb-720c-4c0b-b908-bdbefaf63ec8");
        resultsMap.put("results", null);
        resultsMap.put("provider", null);
        //resultsMap.put("provider", "1");
        //resultsMap.put("results", "Results");

        singleRechargeMapper.closeRequest(resultsMap);
    }
    @Test
    void resolveRecharge() {
        final String id = "04c462eb-720c-4c0b-b908-bdbefaf63ec8";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ResolveRechargeDto dto = new ResolveRechargeDto();
            dto.setRechargeId(id);
            dto.setResolvedBy("Adebola");
            dto.setMessage("Resolved Message");

            rechargeService.resolveRecharge(id, dto);
        });

        log.info(exception.getMessage());
    }

    @Test
    void asyncRefundRecharge() {
        final String rechargeId = "04c462eb-720c-4c0b-b908-bdbefaf63ec8"; // NULL User
        // final String rechargeId = "1b2f9ef5-b9e5-4b34-8e8e-90acd8004617";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            SingleRechargeRequest request = singleRechargeMapper.findById(rechargeId);

            singleRefundRecharge.asyncRefundRecharge(request);
        }
    }

    @Test
    void refundRecharge() {
        final String rechargeId = "04c462eb-720c-4c0b-b908-bdbefaf63ec8"; // NULL User
        // final String rechargeId = "1b2f9ef5-b9e5-4b34-8e8e-90acd8004617";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            SingleRechargeRequest request = singleRechargeMapper.findById(rechargeId);
            // singleRefundRecharge.refundRecharge(rechargeId);
        }
    }

    @Test
    void adminSearch() throws ParseException {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        AdminSearchSingleRechargeDto dto = new AdminSearchSingleRechargeDto();
//        dto.setUserId(id);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
//        final String dateString = "26-04-2022 10:15:55 AM";
//        dto.setPageNumber(1);
//        dto.setPageSize(40);
//
//        Date d = formatter.parse(dateString);
//        dto.setSearchDate(d);
//
////        dto.setSearchProduct("GLO");
////        dto.setSearchRecipient("080");
//        var x = rechargeService.adminSearch(dto);
//        log.info(x);
//        log.info(x.getTotalSize());
    }
    @Test
    void search() throws ParseException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "26-04-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);


        SearchSingleRecharge search = SearchSingleRecharge.builder()
                .userId(id)
                .rechargeId("b")
//                .startDate(d)
//                .product("GLO-DATA")
//                .recipient("070")
                .build();

        var x = rechargeService.search(search, 1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void getRechargeByDateRange() throws IOException, ParseException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            DateRangeDto dto = new DateRangeDto();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
            final String dateString = "27-04-2022 09:15:55";
            dto.setStartDate(formatter.parse(dateString));
            //dto.setEndDate(formatter.parse("12-12-2022 01:00:00"));

            InputStreamResource resource = rechargeService.getRechargeByDateRange(dto);
            File targetFile = new File("/Users/adebola/Downloads/date-range.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void rechargeMTN() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String token = getUserToken(id);


        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);

            k.when(ProviderSecurity::getAccessToken).thenReturn(token);
            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(token);

            SingleRechargeRequestDto dto = new SingleRechargeRequestDto();
            dto.setServiceCode("MTN-AIRTIME");
            dto.setServiceCost(new BigDecimal(15));
            dto.setRecipient("08055572307");

            //final SingleRechargeResponseDto singleRechargeResponseDto = rechargeService.startRecharge(dto);
            //log.info(singleRechargeResponseDto);
        }
    }

    @Test
    void rechargeJed() {
//        RechargeRequestDto dto = new RechargeRequestDto();
//        dto.setServiceCode("JED");
//        dto.setServiceCost(new BigDecimal(1223));
//        dto.setRecipient("44000316354");
//        dto.setTelephone("08012345677");
//        log.info(dto);
//
//        RechargeRequestDto newDto = rechargeService.startRecharge(dto);
//        log.info(newDto);
    }

    @Test
    void getFailedTransactions() {
        var x = rechargeService.getFailedTransactions(1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void getFailedUnresolvedTransactions() {
        var x = rechargeService.getFailedUnresolvedTransactions(1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void getDataPlansEx() {
        var x = rechargeService.getDataPlans("SMILE-DATA");
        log.info(x);
    }

    @Test
    void getDataPlans() {
        String code = "XYZ";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rechargeService.getDataPlans(code);
        });

        String expectedMessage = String.format("Unknown data plan (%s) Or Data Plan is not for DATA", code);
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void getRecharge() {}

    @Test
    void getUserRecharges() {
        //final String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var y = rechargeService.getUserRecharges(ProviderSecurity.getUserId(), 1, 20);
            log.info(y);
        }
    }

    @Test
    void getSingleRecharge() {
        final String id = "04c462eb-720c-4c0b-b908-bdbefaf63ec8";
        var recharge = rechargeService.getRecharge(id);
        assertNotNull(recharge);
        log.info(recharge);
    }

    @Test
    void getSingleVoidRecharge() {
        final String id = "04c462eb-720c-4c0b";
        var recharge = rechargeService.getRecharge(id);
        assertNull(recharge);
    }

    @Test
    void getUserRechargesById() throws IOException {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);
//
//        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(ProviderSecurity::getUserId).thenReturn(id);
//            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
//            log.info(ProviderSecurity.getUserId());
//
//            k.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
//            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(accessToken);
//
//            InputStreamResource resource = rechargeService.getRechargesByUserId(id);
//            File targetFile = new File("test2.xlsx");
//            OutputStream outputStream = new FileOutputStream(targetFile);
//            byte[] buffer = resource.getInputStream().readAllBytes();
//            outputStream.write(buffer);
//
//            log.info(targetFile.getAbsolutePath());
//        }
    }

    @Test
    void getFailedRechrges() throws IOException {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);
//
//        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(ProviderSecurity::getUserId).thenReturn(id);
//            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
//            log.info(ProviderSecurity.getUserId());
//
//            k.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
//            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(accessToken);
//
//            InputStreamResource resource = rechargeService.getFailedRecharges("all");
//            File targetFile = new File("test2.xlsx");
//            OutputStream outputStream = new FileOutputStream(targetFile);
//            byte[] buffer = resource.getInputStream().readAllBytes();
//            outputStream.write(buffer);
//
//            log.info(targetFile.getAbsolutePath());
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
