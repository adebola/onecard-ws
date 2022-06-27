package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.SearchIndividualDto;
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
    Page<NewBulkRechargeRequest> searchByDate(SearchByDate searchByDate);
    Page<IndividualRequest> findPagedBulkIndividualRequests(String id);
    Page<NewBulkRechargeRequest> findBulkRequestByAutoId(Map<String, String> parameters);
    IndividualRequest findIndividualRequestById(IndividualRequestQuery query);
    void saveRequestRetry(IndividualRequestRetry requestRetry);
    Boolean setIndividualRequestSuccess(Integer id);
    Page<IndividualRequest> searchIndividual(SearchIndividualDto dto);
}
