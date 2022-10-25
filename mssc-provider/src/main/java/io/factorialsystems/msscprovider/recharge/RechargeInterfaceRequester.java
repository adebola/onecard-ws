package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RechargeInterfaceRequester {
    private final FactoryProducer producer;
    private final ParameterCache parameterCache;

    // Common Code - Get the Appropriate Recharge Interface Based on Required Service
    public Optional<RechargeParameters> getRecharge(Integer serviceId) {
        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(serviceId);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            final String rechargeProviderCode = parameter.getRechargeProviderCode();
            final String serviceAction = parameter.getServiceAction();

            if (rechargeProviderCode == null || serviceAction == null) {
                return Optional.empty();
            }

            AbstractFactory factory = producer.getFactory(rechargeProviderCode);

            if (factory == null) {
                return Optional.empty();
            }

            RechargeParameters rechargeParameters = RechargeParameters.builder()
                    .recharge(factory.getRecharge(serviceAction))
                    .serviceAction(serviceAction)
                    .rechargeProviderCode(rechargeProviderCode)
                    .rechargeProviderId(parameter.getRechargeProviderId())
                    .build();

            return Optional.of(rechargeParameters);
        }

        return Optional.empty();
    }

    // Common Code - Get the Appropriate ReQuery Interface Based on Required Service
    public Optional<ReQuery> getReQuery(Integer serviceId) {

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(serviceId);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);

            return Optional.ofNullable(factory.getReQuery());
        }

        return Optional.empty();
    }
}
