package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;

public interface ExtraDataEnquiry {
    ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto);
}
