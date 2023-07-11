package io.factorialsystems.msscprovider.recharge.factory;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FactoryProducer {
    private final Map<String, AbstractFactory> factoryMap = new HashMap<>();

    private static final String RINGO_FACTORY_LABEL = "Ringo";
    public static final String CROWN_FACTORY_LABEL = "Crown";
    private static final String ENERGIZE_FACTORY_LABEL = "Energize";
    private static final String SMILE_FACTORY_LABEL = "Smile";
    private static final String ONECARD_FACTORY_LABEL = "Onecard";

    public FactoryProducer(RingoRechargeFactory ringoRechargeFactory, EKEDPRechargeFactory ekedpRechargeFactory,
                           JedRechargeFactory jedRechargeFactory, SmileRechargeFactory smileRechargeFactory,
                           OnecardRechargeFactory onecardRechargeFactory) {

        factoryMap.put(RINGO_FACTORY_LABEL, ringoRechargeFactory);
        factoryMap.put(CROWN_FACTORY_LABEL, ekedpRechargeFactory);
        factoryMap.put(ENERGIZE_FACTORY_LABEL, jedRechargeFactory);
        factoryMap.put(SMILE_FACTORY_LABEL, smileRechargeFactory);
        factoryMap.put(ONECARD_FACTORY_LABEL, onecardRechargeFactory);
    }

    public AbstractFactory getFactory(String factoryType) {
        return Optional.ofNullable(factoryMap.get(factoryType))
                .orElseThrow(() -> new RuntimeException(String.format("Unknown Factory Type %s", factoryType)));
    }
}
