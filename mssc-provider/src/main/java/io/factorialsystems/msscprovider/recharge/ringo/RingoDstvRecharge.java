package io.factorialsystems.msscprovider.recharge.ringo;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoDstvRecharge implements Recharge, ParameterCheck, ExtraDataEnquiry {
    private final DstvHelper dstvHelper;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        return dstvHelper.recharge(request);
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null && request.getRecipient() != null && request.getProductId() != null && request.getName() != null;
    }

    @Override
    public ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto) {
        return dstvHelper.validateCable(dto);
    }
}
