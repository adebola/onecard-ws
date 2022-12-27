package io.factorialsystems.msscprovider.recharge.ringo;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.ExtraDataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "dstv-gotv-plans", key="{#dto.recipient, #dto.serviceCode}")
    public ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto) {
        log.info(String.format("Requesting Extra Data Plans for Recipient (%s) for Service (%s)", dto.getRecipient(), dto.getServiceCode()));
        return dstvHelper.validateCable(dto);
    }
}
