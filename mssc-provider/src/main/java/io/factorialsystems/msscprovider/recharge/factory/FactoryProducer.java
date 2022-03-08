package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.recharge.factory.telco.GloRechargeFactory;
import io.factorialsystems.msscprovider.recharge.factory.telco.MtnRechargeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactoryProducer {

    public AbstractFactory getFactory(String factoryType) {
        FactoryType type = FactoryType.valueOf(factoryType);
        switch (type){
            case ENERGYIZE:
                return new JedRechargeFactory();
            case RINGO:
                return new RingoRechargeFactory();
            case CROWN:
                return new EKEDPRechargeFactory();
            case MTN:
                return new MtnRechargeFactory();
            case GLO:
                return new GloRechargeFactory();

            default: return null;
        }
    }
}
