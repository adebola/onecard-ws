package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoElectricVerifyResponse {
    private String meterNo;
    private String customerName;
    private String customerAddress;
    private String customerDistrict;
    private String phoneNumber;
    private String type;
    private String disco;
    private String status;
    private String minimumPayable;
    private String outstandingAmount;
    private String message;
}
