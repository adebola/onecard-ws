package io.factorialsystems.msscpayments.payment.paystack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyTransactionResponse {
    private Boolean status;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data {
        private String id;
        private String domain;
        private String status;
        private String reference;
        private Integer amount;
        private String message;
        private String gateway_response;
        private String paid_at;
        private String created_at;
    }
}
