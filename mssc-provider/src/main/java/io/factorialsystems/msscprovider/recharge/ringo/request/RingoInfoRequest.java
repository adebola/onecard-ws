package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RingoInfoRequest {
    private String serviceCode;
}
