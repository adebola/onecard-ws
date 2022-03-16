package io.factorialsystems.msscprovider.recharge.ringo.request;

public enum CableType {
    DSTV("DSTV"), GOTV("GOTV"), STARTIMES("STARTIMES");
    private final String value;
    CableType(String value){this.value = value;}
    public String getValue() {return value;}
}