package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduledRechargeMapper {
    NewScheduledRechargeRequest findById(String id);
    List<NewScheduledRechargeRequest> findOpenRequests();
    Page<NewScheduledRechargeRequest> findRequestByUserId(String id);
    void save(NewScheduledRechargeRequest request);
    void closeRequest(String id);
    void saveRecipients(List<IndividualRequest> requests);
    Page<NewScheduledRechargeRequest> searchByDate(SearchByDate s);
    void setBulkRequestId(Map<String, String> recipientsMap);
    List<NewScheduledRechargeRequest> findByUserIdAndDateRange(DateRangeDto dto);
}
