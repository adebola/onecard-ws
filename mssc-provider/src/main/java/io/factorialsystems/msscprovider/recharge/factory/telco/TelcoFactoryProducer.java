package io.factorialsystems.msscprovider.recharge.factory.telco;

import io.factorialsystems.msscprovider.service.telcos.TelcoType;
import org.springframework.stereotype.Component;

@Component
public class TelcoFactoryProducer {

    public TelcoAbstractFactory getFactory(TelcoType telcoType) {

        switch (telcoType){
            case GLO:
                return new GloRechargeFactory();

            case MTN:
                return new MtnRechargeFactory();

            case AIRTEL:
                return new AirtelRechargeFactory();

            case ETISALAT:
                return new EtisalatRechargeFactory();

            default: return null;
        }
    }
}
