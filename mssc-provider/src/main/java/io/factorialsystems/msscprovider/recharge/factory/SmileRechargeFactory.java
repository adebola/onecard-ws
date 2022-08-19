package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.smile.SmileDataRecharge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmileRechargeFactory extends AbstractFactory {

    @Override
    public Recharge getRecharge(String action) {
        return ApplicationContextProvider.getBean(SmileDataRecharge.class);
    }

    @Override
    public DataEnquiry getPlans(String action) {
        return ApplicationContextProvider.getBean(SmileDataRecharge.class);
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {
        throw new RuntimeException("Smile ExtraDataPlans Not Implemented");
    }

    @Override
    public ParameterCheck getCheck(String action) {
        return ApplicationContextProvider.getBean(SmileDataRecharge.class);
    }

    @Override
    public Balance getBalance() {
        return ApplicationContextProvider.getBean(SmileDataRecharge.class);
    }

    @Override
    public ReQuery getReQuery() {
        throw new RuntimeException("Smile ReQuery Not Implemented");
    }
}
