package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface NewBulkRechargeMapper {
    NewBulkRechargeRequest findBulkRechargeById(String id);
    List<IndividualRequest> findBulkIndividualRequests(String id);
    Page<IndividualRequest> findBulkIndividualRequestsByScheduleId(String id);
    void saveBulkRecharge(NewBulkRechargeRequest request);
    void saveBulkIndividualRequests(List<IndividualRequest> requests);
    void closeRequest(String id);
    void failIndividualRequest(IndividualRequestFailureNotification n);
    Page<NewBulkRechargeRequest> findBulkRequestByUserId(String id);
    Page<NewBulkRechargeRequest> searchByDate(Timestamp ts);
    Page<IndividualRequest> findPagedBulkIndividualRequests(String id);
}
