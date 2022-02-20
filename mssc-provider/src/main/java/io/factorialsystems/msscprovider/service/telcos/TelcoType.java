package io.factorialsystems.msscprovider.service.telcos;

public enum TelcoType {
    MTN("MTN"), AIRTEL("AIRTEL"), ETISALAT("ETISALAT"), GLO("GLO");
    private final String value;

    TelcoType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}