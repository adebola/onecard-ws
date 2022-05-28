package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RequestTransactionDto {
    private String userId;
    private Integer serviceId;
    private BigDecimal serviceCost;
    private String requestId;
    private String transactionDate;
    private String recipient;
}
