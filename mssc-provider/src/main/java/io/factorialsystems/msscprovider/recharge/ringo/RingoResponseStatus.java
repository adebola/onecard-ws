package io.factorialsystems.msscprovider.recharge.ringo;

public enum RingoResponseStatus {
    SUCCESS("200");

    private String value;
    RingoResponseStatus(String value){
        this.value = value;
    }

    public String getValue() {return value;}
}