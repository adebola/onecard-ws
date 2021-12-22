package io.factorialsystems.msscprovider.recharge.jed.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JedPaymentResponse {
    private String status;
    private String message;
    private PayDetails payDetails;

    @Data
    static public class PayDetails {
        private String transaction;
        private String accountNumber;
        private String meterNumber;
        private String amount;
        private String token;
        private String units;
        private String tariffRate;
        private String vat;
        private String outstandingPaid;
    }
}
