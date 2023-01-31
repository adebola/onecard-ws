package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.SingleResolve;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequestRetry;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import io.factorialsystems.msscprovider.dto.CombinedRequestDto;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.search.SearchSingleFailedRechargeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SingleRechargeMapper {
    List<SingleRechargeRequest> findByUserId(String id);

    SingleRechargeRequest findById(String id);

    void save(SingleRechargeRequest request);

    List<RechargeFactoryParameters> factory(Integer id);

    void closeRequest(Map<String, String> requestMap);

    void closeAndFailRequest(Map<String, String> requestMap);

    Page<SingleRechargeRequest> findRequestsByUserId(String id);

    List<SingleRechargeRequest> findByUserIdAndDateRange(DateRangeDto dto);

    Page<SingleRechargeRequest> search(SearchSingleRecharge search);

    Page<SingleRechargeRequest> adminFailedSearch(SearchSingleFailedRechargeDto dto);

    Boolean setEmailId(Map<String, String> parameters);

    SingleRechargeRequestRetry findRequestRetryById(String id);

    void saveRetryRequest(SingleRechargeRequestRetry requestRetry);

    Boolean saveSuccessfulRetry(Map<String, String> parameters);

    Boolean saveRefund(Map<String, String> refundMap);

    void saveResolution(SingleResolve resolve);

    Boolean resolveRequest(Map<String, String> rechargeMap);

    Page<SingleRechargeRequest> findFailedRequests();

    List<SingleRechargeRequest> findListFailedRequests();

    Page<SingleRechargeRequest> findFailedUnResolvedRequests();

    List<SingleRechargeRequest> findListUnresolvedFailedRequests();

    Boolean failRequest(Map<String, String> parameters);

    List<SingleRechargeRequest> findSingleByUserIdAndDateRange(CombinedRequestDto dto);

    List<SingleRechargeRequest> findSingleRechargeByCriteria(RechargeReportRequest request);
}
