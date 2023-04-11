package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchIndividualDto;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@CommonsLog
class BulkRechargeServiceTest {
    @Autowired
    private NewBulkRechargeService service;
    @Autowired
    BulkRechargeMapper newBulkRechargeMapper;

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Test
    void testSaveResults() {
        Map<String, String> resultsMap = new HashMap<>();
        resultsMap.put("id", "1");
        //resultsMap.put("results", null);
        //resultsMap.put("results", "Results");
        resultsMap.put("provider", "1");

        newBulkRechargeMapper.saveResults(resultsMap);
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

            InputStreamResource resource = service.getRechargeByDateRange(dto);
            File targetFile = new File("bulk-date-range.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }


    @Test
    void findFailedRecharges() {
        var x = service.getFailedRequests(1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void findFailedUnresolvedRecharges() {
        var x = service.getFailedUnresolvedRequests(1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }
    @Test
    void resolveBulkRechargeFail() {
        final String id = "04c462eb-720c-4c0b-b908-bdbefaf63ec8";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ResolveRechargeDto dto = new ResolveRechargeDto();
            dto.setRechargeId(id);
            dto.setResolvedBy("Adebola");
            dto.setMessage("Resolved Message");


            var x = service.resolveRecharges(id, dto);
            log.info(x);
        });

        log.info(exception.getMessage());
    }

    @Test
    void resolveBulkRecharges() {
        final String id = "268c0450-172c-4cbd-aad5-9368ace533a6";

        ResolveRechargeDto dto = new ResolveRechargeDto();
        dto.setRechargeId(id);
        dto.setResolvedBy("Adebola");
        dto.setMessage("Resolved Message");


        var x = service.resolveRecharges(id, dto);
        log.info(x);
    }

    @Test
    void resolveBulkRecharge() {
        final int individualId = 42;
        final String id = "3d3a01bf-b07c-4f03-bf58-6d55679fb174";

        ResolveRechargeDto dto = new ResolveRechargeDto();

        dto.setRechargeId(id);
        dto.setResolvedBy("Adebola");
        dto.setMessage("Resolved Message");
        dto.setIndividualId(individualId);

        var x = service.resolveRecharges(id, dto);
        log.info(x);
    }



    @Test
    void refundRechargeRequest() {
        //final String rechargeId = "04c462eb-720c-4c0b-b908-bdbefaf63ec8"; // NULL User
        final String rechargeId = "158f4d0b-19be-4d8d-8c83-398383890188";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            service.refundFailedRecharges(rechargeId);
        }
    }

    @Test
    void retryFailedRecharges() {
        String id = "158f4d0b-19be-4d8d-8c83-398383890188";
        //service.retryFailedRecharges(id);
    }

    @Test
    void findRequestRetryById() {
        final String id = "6f6e1014-457c-4670-b738-d673f2bdc871";

        IndividualRequestRetry requestRetry = newBulkRechargeMapper.findRequestRetryById(id);
        assertNotNull(requestRetry);
        log.info(requestRetry);
    }

    @Test
    void saveRetryRequest() {
        IndividualRequestRetry requestRetry = IndividualRequestRetry.builder()
                .recipient("08055572307")
                .requestId(1)
                .retriedBy("adebola")
                .id(UUID.randomUUID().toString())
                .statusMessage("status")
                .successful(true)
                .build();

        newBulkRechargeMapper.saveRetryRequest(requestRetry);
    }

    @Test
    void saveSuccessfulRetry() {
        final String id = "6f6e1014-457c-4670-b738-d673f2bdc871";
        Map<String, String> map = new HashMap<>();

        map.put("id", String.valueOf(1));
        map.put("retryId", id);
        map.put("provider", "1");
        map.put("results", "it is well");

        Boolean b = newBulkRechargeMapper.saveSuccessfulRetry(map);
        assertEquals(b, true);
    }

    @Test
    void saveService() {


//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);
//
//        String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
//        log.info(s);
//
//        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(ProviderSecurity::getUserId).thenReturn(id);
//            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
//            log.info(ProviderSecurity.getUserId());
//
//            k.when(ProviderSecurity::getAccessToken).thenReturn(accessToken);
//            assertThat(ProviderSecurity.getAccessToken()).isEqualTo(accessToken);
//
//            List<IndividualRequestDto> list = new ArrayList<>(1);
//
//            IndividualRequestDto individualRequestDto = new IndividualRequestDto();
//            individualRequestDto.setServiceCode("GLO-AIRTIME");
//            individualRequestDto.setServiceCost(new BigDecimal(1000));
//            individualRequestDto.setRecipient("08055572307");
//
//            list.add(individualRequestDto);
//
////            IndividualRequestDto individualRequestDto2 = new IndividualRequestDto();
////            individualRequestDto2.setServiceCode("GLO-AIRTIME");
////            individualRequestDto2.setServiceCost(new BigDecimal(300));
////            individualRequestDto2.setRecipient("09055572307");
////
////            list.add(individualRequestDto2);
//
//            NewBulkRechargeRequestDto dto = new NewBulkRechargeRequestDto();
//            dto.setRecipients(list);
//            dto.setPaymentMode("wallet");
//            dto.setTotalServiceCost(new BigDecimal(1000));
//
////            var x = service.saveService(dto);
////            log.info(x);
//        }
    }

    @Test
    void saveService_for_AutoRecharge() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        IndividualRequestDto individualRequestDto1 = new IndividualRequestDto();
        individualRequestDto1.setServiceCode("MTN-AIRTIME");
        individualRequestDto1.setRecipient("08033356709");
        individualRequestDto1.setServiceCost(BigDecimal.valueOf(2000));

        IndividualRequestDto individualRequestDto2 = new IndividualRequestDto();
        individualRequestDto2.setServiceCode("GLO-AIRTIME");
        individualRequestDto2.setRecipient("08055572307");
        individualRequestDto2.setServiceCost(BigDecimal.valueOf(1000));

        List<IndividualRequestDto> requestDtos = new ArrayList<>(Arrays. asList(individualRequestDto1, individualRequestDto2));

        NewBulkRechargeRequestDto requestDto = new NewBulkRechargeRequestDto();
        requestDto.setRecipients(requestDtos);
        requestDto.setAutoRequestId(id);
        requestDto.setPaymentMode("wallet");

        //service.saveService(requestDto, Optional.of(id));
    }

    @Test
    void searchByDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "23-02-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);


        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var x = service.searchByDate(d, 1, 20);
            log.info(x);
            log.info(x.getTotalSize());
        }
    }

    @Test
    void search() throws ParseException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        SearchBulkRechargeDto dto = new SearchBulkRechargeDto();
        dto.setUserId(id);
        dto.setPageNumber(1);
        dto.setPageSize(20);
        //dto.setSearchId("15");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "15-06-2022 10:15:55 AM";
        Date d = formatter.parse(dateString);
        dto.setSearchDate(d);


        var x = service.search(dto);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void getUserRecharges() {

        //final String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var y = service.getUserRecharges(ProviderSecurity.getUserId(), 1, 20);
            log.info(y);
        }
    }

    @Test
    void getBulkIndividualRequests() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        var x = service.getBulkIndividualRequests(id, 1, 20);
        log.info(x);
    }

    @Test
    void runRechargeFail() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        RechargeStatus rechargeStatus = new RechargeStatus("Recharge Failed", HttpStatus.INTERNAL_SERVER_ERROR, "");
        Recharge recharge = Mockito.mock(Recharge.class);
        Mockito.when(recharge.recharge(any())).thenReturn(rechargeStatus);

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            AsyncRechargeDto asyncRechargeDto = AsyncRechargeDto.builder()
                    .id(id)
                    .email(ProviderSecurity.getEmail())
                    .build();

            service.runBulkRecharge(asyncRechargeDto);

            var y = service.getBulkIndividualRequests(id, 1, 20);
            assertNotNull(y);
            assert (y.getList().size() > 0);
            assert (y.getList().get(0).getFailedMessage().equals("Recharge Failed"));
            assert (y.getList().get(0).getFailed().equals(true));

            log.info(y);
        }
    }

    @Test
    void runSimpleRechargeFail() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        IndividualRequestFailureNotification notification = IndividualRequestFailureNotification
                .builder()
                .errorMsg("Recharge Failed")
                .id(5)
                .build();

        newBulkRechargeMapper.failIndividualRequest(notification);
        notification.setId(6);
        newBulkRechargeMapper.failIndividualRequest(notification);

        var y = service.getBulkIndividualRequests(id, 1, 20);
        assertNotNull(y);
        assert (y.getList().size() > 0);
        assert (y.getList().get(0).getFailedMessage().equals("Recharge Failed"));
        assert (y.getList().get(0).getFailed().equals(true));

        log.info(y);
    }

    @Test
    void uploadFile() {
        final String fileName = "/Users/adebola/Downloads/Larfarge-2-plus.xlsx";
        File file = new File(fileName);

        UploadFile uploadFile = new UploadFile(file, fileName);
        ExcelReader excelReader = new ExcelReader(uploadFile);
    }

    @Test
    void generateExcelFile() throws IOException {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String bulkId = "158f4d0b-19be-4d8d-8c83-398383890188";
        final String bulkId = "268c0450-172c-4cbd-aad5-9368ace533a6";


        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            InputStreamResource resource = new InputStreamResource(service.generateExcelFile(bulkId));
            File targetFile = new File("test2.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    public void searchIndividual() {
        SearchIndividualDto dto = new SearchIndividualDto();

        dto.setBulkId("158f4d0b-19be-4d8d-8c83-398383890188");
//        dto.setRecipient("08055");
        //dto.setStatus(false);
        dto.setProduct("9");
        var x = service.searchIndividual(dto, 1, 20);

        log.info("Size of Request is " + x.getTotalSize());
        log.info(x);
    }

    @Test
    public void getUserRechargesByAutoRequestId() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String autoId = "02d06cda-64dd-4dbf-8dbe-ffd90dbb1f36";

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var x = service.getUserRechargesByAutoRequestId(autoId, 1, 20);
            log.info("Size " + x.getTotalSize());
        }

    }

    @Test
    public void findByCriteria() {
        RechargeReportRequest request = new RechargeReportRequest();
        Date d = new Date();
        request.setStartDate(d);
        request.setEndDate((d));
        List<NewBulkRechargeRequest> bulkRechargeByCriteria = newBulkRechargeMapper.findBulkRechargeByCriteria(request);
        assertNotNull(bulkRechargeByCriteria);
        log.info(bulkRechargeByCriteria);
        log.info(String.format("Size %d", bulkRechargeByCriteria.size()));
    }


    @Test
    void retryFailedRecharge() {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);
//
//        RechargeStatus rechargeStatus = new RechargeStatus("Recharge Failed", HttpStatus.INTERNAL_SERVER_ERROR);
//        Recharge recharge = Mockito.mock(Recharge.class);
//        Mockito.when(recharge.recharge(any())).thenReturn(rechargeStatus);
//
//        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
//            k.when(Constants::getUserId).thenReturn(id);
//            assertThat(Constants.getUserId()).isEqualTo(id);
//
//            k.when(Constants::getAccessToken).thenReturn(accessToken);
//            assertThat(Constants.getAccessToken()).isEqualTo(accessToken);
//
//            log.info(Constants.getUserId());
//            log.info(Constants.getAccessToken());
//
//            var x = service.retryFailedRecharge(1);
//            log.info(x.getMessage());
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