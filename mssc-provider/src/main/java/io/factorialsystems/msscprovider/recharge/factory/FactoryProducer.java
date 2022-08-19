package io.factorialsystems.msscprovider.recharge.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactoryProducer {

    public AbstractFactory getFactory(String factoryType) {
        if (factoryType.equalsIgnoreCase("Ringo")) {
            return new RingoRechargeFactory();
        } else if (factoryType.equalsIgnoreCase("Crown")) {
            return new EKEDPRechargeFactory();
        } else if (factoryType.equalsIgnoreCase("Energize")) {
            return new JedRechargeFactory();
        } else if (factoryType.equalsIgnoreCase("Smile")) {
            return new SmileRechargeFactory();
        }

        throw new RuntimeException(String.format("Unknown Factory Type %s", factoryType));
    }
}
