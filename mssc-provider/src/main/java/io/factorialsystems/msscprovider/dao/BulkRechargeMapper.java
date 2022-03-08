package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.BulkRecipient;
import io.factorialsystems.msscprovider.domain.Telephone;
import io.factorialsystems.msscprovider.domain.rechargerequest.BulkRechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BulkRechargeMapper {
    BulkRechargeRequest findById(String id);
    void save(BulkRechargeRequest request);
    List<Telephone> findRecipients(String id);
    void saveRecipients(List<BulkRecipient> recipients);
    void closeRequest(String id);
    void updateBulkRechargeId(Map<String, String> mapRecipient);
}
