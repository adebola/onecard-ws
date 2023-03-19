package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.rechargerequest.*;
import io.factorialsystems.msscprovider.domain.query.SearchByDate;
import io.factorialsystems.msscprovider.domain.query.SearchByString;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AutoRechargeMapper {
    void saveAutoRecharge(AutoRechargeRequest request);
    void saveAutoRecurringEvents(List<AutoRecurringEvent> events);
    void updateAutoRecurringEvents(List<AutoRecurringEvent> events);
    void saveRecipients(List<AutoIndividualRequest> requests);
    AutoRechargeRequest findAutoRechargeById(String id);
    List<AutoRecurringEvent> findEnabledRecurringEventsByAutoId(String id);
    List<AutoIndividualRequest> findBulkIndividualRequests(String id);
    Page<ShortAutoRechargeRequest> findAutoRechargeByUserId(String id);
    void updateAutoRecharge(AutoRechargeRequest request);
    void deleteAutoRecharge(String id);
    List<AutoRunEvent> todaysWeeklyRuns(Map<String, String> params);
    List<AutoRunEvent> todaysMonthlyRuns(Map<String, String> params);
    List<AutoRunEvent> lastDayMonthlyRuns (Map<String, String> params);
    void saveRanEvent(AutoEventRan autoEventRan);
    List<AutoRecurringEvent>  disableAndLoadRecurringEventsByAutoId(String id);
    Page<ShortAutoRechargeRequest> searchByDate(SearchByDate s);
    Page<ShortAutoRechargeRequest> searchByName(SearchByString s);
    List<ShortAutoRechargeRequest> findByUserIdAndDateRange(DateRangeDto dto);
    void deleteRecipientsByAutoRechargeId(String id);
}
