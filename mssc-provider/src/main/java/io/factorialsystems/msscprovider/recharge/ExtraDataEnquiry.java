package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.dto.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.ExtraPlanRequestDto;

public interface ExtraDataEnquiry {
    ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto);
}
