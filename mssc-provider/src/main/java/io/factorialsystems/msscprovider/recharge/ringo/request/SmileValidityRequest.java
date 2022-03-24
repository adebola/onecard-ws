package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmileValidityRequest {
    private String serviceCode;
    private String account;
    private String type;
}
