package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BulkRechargeMapperTest {

    @Autowired
    BulkRechargeMapper bulkRechargeMapper;

    @Test
    void findBulkRechargeById() {
        final String id = "635eb139-003f-4f8f-b862-1e0bc7a958fd";
        final NewBulkRechargeRequest recharge = bulkRechargeMapper.findBulkRechargeById(id);
        assertThat(recharge).isNotNull();
        assertThat(recharge.getId()).isEqualTo(id);

        log.info("BulkRecharge {}", recharge);
    }

    @Test
    void findBulkIndividualRequests() {
        final String id = "635eb139-003f-4f8f-b862-1e0bc7a958fd";
        final List<IndividualRequest> requests = bulkRechargeMapper.findBulkIndividualRequests(id);

        assertThat(requests).isNotNull();
        assertThat(requests.size()).isGreaterThan(0);
        log.info("Bulk Individual Requests {}", requests);
    }

    @Test
    void findPagedBulkIndividualRequestsByScheduleId() {
    }

    @Test
    void findBulkIndividualRequestsByScheduleId() {
    }

    @Test
    void saveBulkRecharge() {
    }

    @Test
    void saveBulkIndividualRequests() {
    }

    @Test
    void closeRequest() {
    }

    @Test
    void setRunning() {
    }

    @Test
    void setEmailId() {
    }

    @Test
    void failIndividualRequest() {
    }

    @Test
    void findBulkRequestByUserId() {
    }

    @Test
    void findListBulkRequestByUserId() {
    }

    @Test
    void searchByDate() {
    }

    @Test
    void search() {
    }

    @Test
    void findPagedBulkIndividualRequests() {
    }

    @Test
    void findBulkIndividualFailedRequests() {
    }

    @Test
    void findBulkRequestByAutoId() {
    }

    @Test
    void findIndividualRequestByQuery() {
    }

    @Test
    void findIndividualRequestById() {
    }

    @Test
    void setIndividualRequestSuccess() {
    }

    @Test
    void searchIndividual() {
    }

    @Test
    void saveRetryRequest() {
    }

    @Test
    void saveSuccessfulRetry() {
    }

    @Test
    void findRequestRetryById() {
    }

    @Test
    void findRefundTotalByRequestId() {
    }

    @Test
    void saveBulkRefund() {
    }

    @Test
    void saveIndividualRefund() {
    }

    @Test
    void saveResolution() {
    }

    @Test
    void resolveBulkRequest() {
    }

    @Test
    void resolveIndividualRequest() {
    }

    @Test
    void findFailedUnResolvedRequests() {
    }

    @Test
    void findListFailedUnResolvedRequests() {
    }

    @Test
    void findFailedRequests() {
    }

    @Test
    void findListFailedRequests() {
    }

    @Test
    void findFailedIndividuals() {
    }

    @Test
    void findListFailedIndividuals() {
    }

    @Test
    void findFailedUnresolvedIndividuals() {
    }

    @Test
    void findListFailedUnresolvedIndividuals() {
    }

    @Test
    void adminFailedSearch() {
    }

    @Test
    void searchFailedIndividual() {
    }

    @Test
    void saveResults() {
    }

    @Test
    void findBulkByUserIdAndDateRange() {
    }

    @Test
    void findByUserIdToday() {
    }

    @Test
    void individualCount() {
    }

    @Test
    void duplicateRequest() {
    }

    @Test
    void findBulkRechargeByCriteria() {
    }

    @Test
    void findBulkReportIndividualRequests() {
    }

    @Test
    void findBulkIndividualRequestsByCriteria() {
    }
}