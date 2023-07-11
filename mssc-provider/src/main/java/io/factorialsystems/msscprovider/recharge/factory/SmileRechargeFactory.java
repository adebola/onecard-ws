package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.smile.SmileDataRecharge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmileRechargeFactory extends AbstractFactory {
    private final SmileDataRecharge smileDataRecharge;

    @Override
    public Recharge getRecharge(String action) {
        return smileDataRecharge;
    }

    @Override
    public DataEnquiry getPlans(String action) {
        return smileDataRecharge;
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {
        throw new RuntimeException("Smile ExtraDataPlans Not Implemented");
    }

    @Override
    public ParameterCheck getCheck(String action) {
        return smileDataRecharge;
    }

    @Override
    public Balance getBalance() {
        return smileDataRecharge;
    }

    @Override
    public ReQuery getReQuery() {
        throw new RuntimeException("Smile ReQuery Not Implemented");
    }
}
