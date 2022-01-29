package io.factorialsystems.msscpayments.payment.paystack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitializeTransactionResponse {
    private Boolean status;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private String authorization_url;
        private String access_code;
        private String reference;
    }
}
