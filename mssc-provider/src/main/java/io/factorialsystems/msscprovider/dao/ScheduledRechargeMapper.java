package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.BulkRecipient;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduledRechargeMapper {
    List<ScheduledRechargeRequest> findOpenRequests();
    ScheduledRechargeRequest findById(String id);
    void save(ScheduledRechargeRequest request);
    void update (ScheduledRechargeRequest request);
//    List<Telephone> findRecipients(String id);
    void saveRecipients(List<BulkRecipient> recipients);
    void closeRequest(String  id);
}
