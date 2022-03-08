package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewBulkRechargeMapper {
    NewBulkRechargeRequest findBulkRechargeById(String id);
    List<IndividualRequest> findBulkIndividualRequests(String id);
    void saveBulkRecharge(NewBulkRechargeRequest request);
    void saveBulkIndividualRequests(List<IndividualRequest> requests);
    void closeRequest(String id);
}
