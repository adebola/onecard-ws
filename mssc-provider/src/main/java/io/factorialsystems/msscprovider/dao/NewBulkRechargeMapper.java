package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.SingleResolve;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.CombinedRequestDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkFailedRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchIndividualDto;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NewBulkRechargeMapper {
    NewBulkRechargeRequest findBulkRechargeById(String id);

    List<IndividualRequest> findBulkIndividualRequests(String id);

    Page<IndividualRequest> findPagedBulkIndividualRequestsByScheduleId(String id);

    List<IndividualRequest> findBulkIndividualRequestsByScheduleId(String id);

    void saveBulkRecharge(NewBulkRechargeRequest request);

    void saveBulkIndividualRequests(List<IndividualRequest> requests);

    Boolean closeRequest(String id);

    Boolean setRunning(String id);

    Boolean setEmailId(Map<String, String> parameters);

    void failIndividualRequest(IndividualRequestFailureNotification n);

    Page<NewBulkRechargeRequest> findBulkRequestByUserId(String id);

    List<NewBulkRechargeRequest> findListBulkRequestByUserId(String id);

    Page<NewBulkRechargeRequest> searchByDate(SearchByDate searchByDate);

    Page<NewBulkRechargeRequest> search(SearchBulkRechargeDto dto);

    Page<IndividualRequest> findPagedBulkIndividualRequests(String id);

    List<IndividualRequest> findBulkIndividualFailedRequests(String id);

    Page<NewBulkRechargeRequest> findBulkRequestByAutoId(Map<String, String> parameters);

    IndividualRequest findIndividualRequestByQuery(IndividualRequestQuery query);

    IndividualRequest findIndividualRequestById(Integer id);

    Boolean setIndividualRequestSuccess(Integer id);

    Page<IndividualRequest> searchIndividual(SearchIndividualDto dto);

    void saveRetryRequest(IndividualRequestRetry retry);

    Boolean saveSuccessfulRetry(Map<String, String> map);

    IndividualRequestRetry findRequestRetryById(String id);

    Double findRefundTotalByRequestId(String id);

    Boolean saveBulkRefund(Map<String, String> map);

    Boolean saveIndividualRefund(Map<String, String> map);

    void saveResolution(SingleResolve resolve);

    Boolean resolveBulkRequest(Map<String, String> map);

    Boolean resolveIndividualRequest(Map<String, String> map);

    Page<NewBulkRechargeRequest> findFailedUnResolvedRequests();

    List<NewBulkRechargeRequest> findListFailedUnResolvedRequests();

    Page<NewBulkRechargeRequest> findFailedRequests();

    List<NewBulkRechargeRequest> findListFailedRequests();

    Page<IndividualRequest> findFailedIndividuals(String id);

    List<IndividualRequest> findListFailedIndividuals(String id);

    Page<IndividualRequest> findFailedUnresolvedIndividuals(String id);

    List<IndividualRequest> findListFailedUnresolvedIndividuals(String id);

    Page<NewBulkRechargeRequest> adminFailedSearch(SearchBulkFailedRechargeDto dto);

    Page<IndividualRequest> searchFailedIndividual(SearchIndividualDto dto);

    Boolean saveResults(Map<String, String> resultMap);

    List<NewBulkRechargeRequest> findBulkByUserIdAndDateRange(CombinedRequestDto dto);

    List<NewBulkRechargeRequest> findByUserIdToday(Map<String, String> parameterMap);

    Integer individualCount(String id);

    int duplicateRequest(String id);
}
