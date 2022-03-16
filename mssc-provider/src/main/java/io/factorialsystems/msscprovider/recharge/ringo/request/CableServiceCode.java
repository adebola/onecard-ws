package io.factorialsystems.msscprovider.recharge.ringo.request;

public enum CableServiceCode {
    V_TV("V-TV"), P_TV("P-TV");
    private final String value;
    CableServiceCode(String value){this.value = value;}
    public String getValue() {return value;}
}