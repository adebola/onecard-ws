package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;

import java.util.List;

public interface DataEnquiry {
    List<DataPlanDto> getDataPlans(String planCode);
    DataPlanDto getPlan(String id, String planCode);
}
