package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ProviderTransactionDto {
    private Date date;
    private String service;
    private BigDecimal cost;
    private String provider;
    private String userName;
    private String narrative;
}
