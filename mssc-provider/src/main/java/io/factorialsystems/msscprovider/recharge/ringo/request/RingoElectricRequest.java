package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RingoElectricRequest {
    private String serviceCode;
    private String disco;
    private String meterNo;
    private String type;
    private String amount;
    private String phonenumber;
    private String request_id;
}
