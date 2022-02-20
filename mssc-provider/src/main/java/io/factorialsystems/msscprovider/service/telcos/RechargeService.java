package io.factorialsystems.msscprovider.service.telcos;

import io.factorialsystems.msscprovider.dto.ServerResponse;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestResponseDto;

public interface RechargeService {
    Boolean isParametersChecked(SingleRechargeRequestDto singleRechargeRequest);
    ServerResponse startRecharge(SingleRechargeRequestDto singleRechargeRequestDto);
    ServerResponse completeRecharge(String rechargeRequestId);
}