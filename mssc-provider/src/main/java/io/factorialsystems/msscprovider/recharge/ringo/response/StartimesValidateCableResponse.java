package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@AllArgsConstructor
public class StartimesValidateCableResponse {
    private String customerName;
    private String product;
    private String message;
    private String status;
    private String smartCardNo;
    private String type;
}
