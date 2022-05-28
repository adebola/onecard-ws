package io.factorialsystems.msscprovider.cache;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParameterCache {
    private final SingleRechargeMapper singleRechargeMapper;

    @Cacheable("parameters")
    public List<RechargeFactoryParameters> getFactoryParameter(Integer factoryType) {
        return singleRechargeMapper.factory(factoryType);
    }
}
