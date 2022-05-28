package io.factorialsystems.msscprovider.recharge.factory;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.jed.JedElectricRecharge;

import java.util.HashMap;
import java.util.Map;

public class JedRechargeFactory extends AbstractFactory {

    public static final Map<String, String> messages = new HashMap<>();
    public static final String TOKEN = "TOKEN";
    public static final String PRIVATE_KEY = "PRIVATE-KEY";

    static {
        messages.put("000", "Invalid Client");
        messages.put("100", "Successful Recharge");
        messages.put("101", "Invalid");
        messages.put("102", "Insufficient Wallet Balance");
        messages.put("103", "Less/More Minimum/Max Amount");
        messages.put("104", "Customer does not exist");
        messages.put("105", "Amount less Outstanding");
        messages.put("106", "Missing Parameter");
        messages.put("107", "Could not authenticate user");
        messages.put("108", "Payment has already been made");
        messages.put("109", "Payment Not Successful");
    }

    @Override
    public Recharge getRecharge(String action) {
        if (action.equalsIgnoreCase("ELECTRICITY")) {
            return ApplicationContextProvider.getBean(JedElectricRecharge.class);
        }

        return null;
    }

    @Override
    public DataEnquiry getPlans(String code) {
       return null;
    }

    @Override
    public ExtraDataEnquiry getExtraPlans(String action) {

        if (action.equalsIgnoreCase("JED")) {
            return ApplicationContextProvider.getBean(JedElectricRecharge.class);
        }

        return null;
    }

    @Override
    public ParameterCheck getCheck(String s) {
        return ApplicationContextProvider.getBean(JedElectricRecharge.class);
    }

    @Override
    public Balance getBalance() {
        return null;
    }
}
