package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SpectranetRequest {
    private String serviceCode;
    private BigDecimal amount;
    private String type;
    private String request_id;
   private String pinNo;
}
