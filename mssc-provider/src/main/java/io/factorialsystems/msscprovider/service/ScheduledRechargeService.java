package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.domain.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestResponseDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledRechargeService {
    private static final String BULK_RECHARGE = "bulk";
    private static final String SINGLE_RECHARGE = "single";

    private final RechargeMapstructMapper rechargeMapstructMapper;

    public ScheduledRechargeRequestResponseDto startRecharge(ScheduledRechargeRequestDto dto) {

        boolean result = false;

        if (dto.getRechargeType().equals(BULK_RECHARGE)) {
            result = checkBulkRequestParameters(dto);
        } else if (dto.getRechargeType().equals(SINGLE_RECHARGE)) {
            result = checkSingleRequestParameters(dto);
        } else {
            final String message =
                    String.format("Invalid Recharge Type (%s), recharge type should either be \"single\" or \"bulk\"", dto.getRechargeType());
            throw new RuntimeException(message);
        }

        return null;
    }

    private Boolean checkSingleRequestParameters(ScheduledRechargeRequestDto dto) {
        SingleRechargeRequestDto singleRechargeRequestDto = rechargeMapstructMapper.scheduledToSingle(dto);
        SingleRechargeRequest request =
                rechargeMapstructMapper.rechargeDtoToRecharge(singleRechargeRequestDto);

        if (request != null) {
            return SingleRechargeService.checkParameters(request, singleRechargeRequestDto);
        }

        return false;
    }

    private Boolean checkBulkRequestParameters(ScheduledRechargeRequestDto dto) {
        return true;

    }
}
