package io.factorialsystems.msscprovider.recharge.factory;

public enum FactoryType{
    RINGO("Ringo"), CROWN("crown"),
    ENERGYIZE("Energize"), MTN("mtn"),
    GLO("glo"), AIRTEL("airtel"),
    ETISALAT("etisalat");

    private final String value;

    FactoryType(String value){
        this.value = value;
    }
}
