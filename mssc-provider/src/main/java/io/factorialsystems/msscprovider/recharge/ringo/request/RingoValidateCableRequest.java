package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RingoValidateCableRequest {
    private String smartCardNo;
    private String type;
    private String serviceCode;
}
