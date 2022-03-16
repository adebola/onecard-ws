package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoValidateCableRequest {
    private String serviceCode = "V-TV";
    private CableType cableType;
    private CableServiceCode cableServiceCode;
}
