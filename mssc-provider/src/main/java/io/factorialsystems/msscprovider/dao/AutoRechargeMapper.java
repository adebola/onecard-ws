package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.rechargerequest.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AutoRechargeMapper {
    void saveAutoRecharge(AutoRechargeRequest request);
    void saveAutoRecurringEvents(List<AutoRecurringEvent> events);
    void updateAutoRecurringEvents(List<AutoRecurringEvent> events);
    void saveRecipients(List<IndividualRequest> requests);
    AutoRechargeRequest findAutoRechargeById(String id);
    List<AutoRecurringEvent> findEnabledRecurringEventsByAutoId(String id);
    List<IndividualRequest> findBulkIndividualRequests(String id);
    List<ShortAutoRechargeRequest> findAutoRechargeByUserId(String id);
    void updateAutoRecharge(AutoRechargeRequest request);
    void deleteAutoRecharge(String id);
    List<AutoRunEvent> todaysWeeklyRuns(Map<String, String> params);
    List<AutoRunEvent> todaysMonthlyRuns(Map<String, String> params);
    List<AutoRunEvent> lastDayMonthlyRuns (Map<String, String> params);
    void saveRanEvent(AutoEventRan autoEventRan);
    List<AutoRecurringEvent>  disableAndLoadRecurringEventsByAutoId(String id);

    //    List<AutoRecurringEvent> findRecurringEventsByAutoId(String id);
    //    void disableRecurringEventsByAutoId(String id);
    //    void removeRecurringEvents(String id);
}
