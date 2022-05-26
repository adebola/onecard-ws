package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NewScheduledRechargeMapper {
    NewScheduledRechargeRequest findById(String id);
    List<NewScheduledRechargeRequest> findOpenRequests();
    void save(NewScheduledRechargeRequest request);
    void closeRequest(String id);
    void saveRecipients(List<IndividualRequest> requests);
    void setBulkRequestId(Map<String, String> recipientsMap);
}