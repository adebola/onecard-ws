package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SmileRequest {
    private String serviceCode;
    private String account;
    private String type;
    private String request_id;
    private BigDecimal price;
    private String name;
    private String allowance;
    private String validity;
    private String code;
}
