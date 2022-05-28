package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RingoElectricVerifyRequest {
    private String serviceCode;
    private String disco;
    private String meterNo;
    private String type;
}
