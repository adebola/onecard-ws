package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoValidateDstvRequest {
    private String serviceCode = "V-TV";
    private String type = "DSTV";
    private String smartCardNo;
}
