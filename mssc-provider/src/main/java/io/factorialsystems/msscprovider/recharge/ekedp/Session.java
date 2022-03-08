package io.factorialsystems.msscprovider.recharge.ekedp;

public interface Session {
    String startSession();
    String getSession();
    String reFreshSession();
    Boolean logIn(String session);
    void logOut(String session);
}
