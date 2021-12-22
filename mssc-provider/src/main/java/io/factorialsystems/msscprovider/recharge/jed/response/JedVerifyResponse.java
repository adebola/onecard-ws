package io.factorialsystems.msscprovider.recharge.jed.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JedVerifyResponse {
    private String status;
    private String accessCode;
    private Customer customer;

    @Data
    public static class Customer {
        private String accountNumber;
        private String meterNumber;
        private String customerType;
        private String name;
        private String address;
        private String phone;
        private String feeder_33_11_dt;
        private String tariffRate;
        private String outStanding;

    }
}
