package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RingoAirtimeRequest {
    private String serviceCode;
    private String msisdn;
    private String amount;
    private String request_id;
    private String product_id;
}
