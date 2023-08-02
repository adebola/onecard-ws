package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequestRetry;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import io.factorialsystems.msscprovider.dto.CombinedRequestDto;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.search.SearchSingleFailedRechargeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SingleRechargeMapperTest {

    @Autowired
    SingleRechargeMapper singleRechargeMapper;

    @Test
    void findByUserId() {
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        List<SingleRechargeRequest> requests = singleRechargeMapper.findByUserId(request.getUserId());
        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
        assertThat(requests.get(0).getUserId()).isEqualTo(request.getUserId());

        log.info("Requests {}", requests);
    }

    @Test
    void findById() {
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isEqualTo(request.getId());
        assertThat(savedRequest.getUserId()).isEqualTo(request.getUserId());

        log.info("Request {}", savedRequest);
    }

    @Test
    void save() {
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isEqualTo(request.getId());
        assertThat(savedRequest.getUserId()).isEqualTo(request.getUserId());

        log.info("Request {}", savedRequest);
    }

    @Test
    void factory() {
        final List<RechargeFactoryParameters> factory = singleRechargeMapper.factory(1);
        assertThat(factory).isNotNull();
        assertThat(factory.size()).isGreaterThan(0);
        log.info("Factory {}", factory);
    }

    @Test
    void closeRequest() {
        final Integer rechargeProviderId = 1;
        final String results = "Successful";

        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", savedRequest.getId());
        parameters.put("provider", String.valueOf(rechargeProviderId));
        parameters.put("results", results);

        singleRechargeMapper.closeRequest(parameters);

        SingleRechargeRequest updatedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(updatedRequest.getId()).isEqualTo(request.getId());
        assertThat(updatedRequest.getRechargeProviderId()).isEqualTo(rechargeProviderId);
        assertThat(updatedRequest.getResults()).isEqualTo(results);
        assertThat(updatedRequest.getClosed()).isEqualTo(true);

        log.info("Updated Request {}", updatedRequest);
    }


    @Test
    void closeRequest_NULL_Values() {
        final String results = "Successful";
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(savedRequest.getId()).isEqualTo(request.getId());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", savedRequest.getId());
        //parameters.put("provider", String.valueOf(rechargeProviderId));
        parameters.put("results", results);

        singleRechargeMapper.closeRequest(parameters);

        final SingleRechargeRequest rechargeRequest = singleRechargeMapper.findById(savedRequest.getId());
        assertThat(rechargeRequest.getResults()).isEqualTo(results);
        assertThat(rechargeRequest.getRechargeProviderId()).isNull();
        log.info("RechargeRequest {}", rechargeRequest);

    }

    @Test
    void closeAndFailRequest() {
        final String results = "Failed";
        final String failedMessage = "Failed Message";

        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", savedRequest.getId());
        parameters.put("failed_message", failedMessage);
        parameters.put("results", results);

        singleRechargeMapper.closeAndFailRequest(parameters);

        SingleRechargeRequest updatedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(updatedRequest.getId()).isEqualTo(request.getId());
        assertThat(updatedRequest.getResults()).isEqualTo(results);
        assertThat(updatedRequest.getClosed()).isEqualTo(true);
        assertThat(updatedRequest.getFailed()).isEqualTo(true);

        log.info("Updated Request {}", updatedRequest);
    }

    @Test
    void findRequestsByUserId() {
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        Page<SingleRechargeRequest> requests = singleRechargeMapper.findRequestsByUserId(request.getUserId());
        assertThat(requests).isNotNull();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0).getId()).isEqualTo(request.getId());
        assertThat(requests.get(0).getUserId()).isEqualTo(request.getUserId());

        log.info("Requests {}", requests);
    }

    @Test
    void findByUserIdAndDateRange() throws ParseException {
        DateRangeDto dateRangeDto = new DateRangeDto();
        dateRangeDto.setId("e33b6988-e636-44d8-894d-c03c982d8fa5");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "26-04-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);
        dateRangeDto.setStartDate(d);

        final List<SingleRechargeRequest> requests = singleRechargeMapper.findByUserIdAndDateRange(dateRangeDto);
        assertThat(requests).isNotNull();
        assertThat(requests.size()).isGreaterThan(0);

        log.info("Size is {}", requests.size());
    }

    @Test
    void search() throws ParseException {
        SearchSingleRecharge searchSingleRecharge = new SearchSingleRecharge();
//        searchSingleRecharge.setUserId("e33b6988-e636-44d8-894d-c03c982d8fa5");
        searchSingleRecharge.setUserId("91b1d158-01fa-4f9f-9634-23fcfe72f76a");

        searchSingleRecharge.setRechargeId("a");

        Date d = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH).parse("07-07-2023 00:00:00");
        searchSingleRecharge.setStartDate(d);

        final Page<SingleRechargeRequest> search = singleRechargeMapper.search(searchSingleRecharge);
        assertThat(search).isNotNull();
        assertThat(search.getResult().size()).isEqualTo(2);

        log.info("Size of Search {}", search.size());
        log.info("Search is {}", search);
    }

    @Test
    void adminFailedSearch() throws ParseException {
        SearchSingleFailedRechargeDto rechargeDto = new SearchSingleFailedRechargeDto();
        rechargeDto.setSearchRecipient("080");
        rechargeDto.setSearchProduct("GLO");

        Date d = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH).parse("10-01-2022 00:00:00");
        rechargeDto.setSearchDate(d);

        final Page<SingleRechargeRequest> requests = singleRechargeMapper.adminFailedSearch(rechargeDto);
        assertThat(requests).isNotNull();
        assertThat(requests.getResult().size()).isEqualTo(7);
    }

    @Test
    void setEmailId() {
        final String emailId = "adeomoboya@gmail.com";
        SingleRechargeRequest request = createRechargeRequest();
        singleRechargeMapper.save(request);

        SingleRechargeRequest savedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isEqualTo(request.getId());
        assertThat(savedRequest.getUserId()).isEqualTo(request.getUserId());
        assertThat(savedRequest.getEmailId()).isNull();

        Map<String, String> emailParams = new HashMap<>();
        emailParams.put("id", savedRequest.getId());
        emailParams.put("emailId", emailId);

        singleRechargeMapper.setEmailId(emailParams);

        SingleRechargeRequest updatedRequest = singleRechargeMapper.findById(request.getId());
        assertThat(updatedRequest).isNotNull();
        assertThat(updatedRequest.getId()).isEqualTo(request.getId());
        assertThat(updatedRequest.getUserId()).isEqualTo(request.getUserId());
        assertThat(updatedRequest.getEmailId()).isEqualTo(emailId);

        log.info("Updated Request {}", updatedRequest);
    }

    @Test
    void findRequestRetryById() {
        final SingleRechargeRequestRetry requestRetry = createRequestRetry();
        singleRechargeMapper.saveRetryRequest(requestRetry);

        final SingleRechargeRequestRetry requestRetryById = singleRechargeMapper.findRequestRetryById(requestRetry.getId());
        assertThat(requestRetryById).isNotNull();
        assertThat(requestRetryById.getId()).isEqualTo(requestRetry.getId());
    }

    @Test
    void saveRetryRequest() {
        final SingleRechargeRequestRetry requestRetry = createRequestRetry();
        singleRechargeMapper.saveRetryRequest(requestRetry);

        final SingleRechargeRequestRetry requestRetryById = singleRechargeMapper.findRequestRetryById(requestRetry.getId());
        assertThat(requestRetryById).isNotNull();
        assertThat(requestRetryById.getId()).isEqualTo(requestRetry.getId());
    }

    @Test
    void saveSuccessfulRetry() {
        final String id = "04c462eb-720c-4c0b-b908-bdbefaf63ec8";

        final SingleRechargeRequestRetry requestRetry = createRequestRetry();
        singleRechargeMapper.saveRetryRequest(requestRetry);

        Map<String, String> retryParams = new HashMap<>();
        retryParams.put("id", id);
        retryParams.put("retryId", requestRetry.getId());

        singleRechargeMapper.saveSuccessfulRetry(retryParams);

        final SingleRechargeRequest request = singleRechargeMapper.findById(id);
        assertThat(request).isNotNull();
        assertThat(request.getRetryId()).isEqualTo(requestRetry.getId());
    }

    @Test
    void saveRefund() {
    }

    @Test
    void saveResolution() {
    }

    @Test
    void resolveRequest() {
    }

    @Test
    void findFailedRequests() {
        final Page<SingleRechargeRequest> requests = singleRechargeMapper.findFailedRequests();
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
        log.info("Size {}", requests.size());
    }

    @Test
    void findListFailedRequests() {
        final List<SingleRechargeRequest> requests = singleRechargeMapper.findListFailedRequests();
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
        log.info("Size {}", requests.size());
    }

    @Test
    void findFailedUnResolvedRequests() {
        final Page<SingleRechargeRequest> requests = singleRechargeMapper.findFailedUnResolvedRequests();
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
        log.info("Size {}", requests.size());
    }

    @Test
    void findListUnresolvedFailedRequests() {
        final List<SingleRechargeRequest> requests = singleRechargeMapper.findListUnresolvedFailedRequests();
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
        log.info("Size {}", requests.size());
    }

    @Test
    void failRequest() {
    }

    @Test
    void findSingleByUserIdAndDateRange() throws ParseException {
        CombinedRequestDto requestDto = new CombinedRequestDto();
        requestDto.setId("91b1d158-01fa-4f9f-9634-23fcfe72f76a");
//        Date d = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH).parse("10-01-2022 00:00:00");
//        requestDto.setStartDate(d);
        final List<SingleRechargeRequest> requests = singleRechargeMapper.findSingleByUserIdAndDateRange(requestDto);
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
    }

    @Test
    void findSingleRechargeByCriteria() {
        RechargeReportRequest reportRequest = new RechargeReportRequest();
        reportRequest.setUserId("91b1d158-01fa-4f9f-9634-23fcfe72f76a");
//        reportRequest.setServiceId(3);
        final List<SingleRechargeRequest> requests = singleRechargeMapper.findSingleRechargeByCriteria(reportRequest);
        assertThat(requests).isNotNull();
        final Optional<SingleRechargeRequest> first = requests.stream().filter(r -> r.getRechargeProviderId() == null).findFirst();
        assertThat(first.isPresent()).isEqualTo(true);
        log.info("Size {}", requests.size());
    }

    private SingleRechargeRequest createRechargeRequest() {
        return SingleRechargeRequest.builder()
                .id(UUID.randomUUID().toString())
                .userId(UUID.randomUUID().toString())
                .recipient("08055572307")
                .serviceId(3)
                .serviceCode("GLO-AIRTIME")
                .paymentMode("wallet")
                .serviceCost(new BigDecimal(1200))
                .build();
    }

    private SingleRechargeRequestRetry createRequestRetry() {
        return SingleRechargeRequestRetry.builder()
                .retriedOn(new Timestamp(System.currentTimeMillis()))
                .retriedBy("adebola")
                .successful(false)
                .recipient("08055572307")
                .id(UUID.randomUUID().toString())
                .requestId("04c462eb-720c-4c0b-b908-bdbefaf63ec8")
                .build();
    }

    @Test
    void testCloseRequest() {
    }
}