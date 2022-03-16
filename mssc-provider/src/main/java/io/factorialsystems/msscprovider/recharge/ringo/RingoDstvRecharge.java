package io.factorialsystems.msscprovider.recharge.ringo;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoDstvRecharge implements Recharge, ParameterCheck, Balance {
    private final DstvService dstvService;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        return dstvService.recharge(request);
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null && request.getRecipient() != null && request.getServiceCost() != null;
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(0);
    }
}
