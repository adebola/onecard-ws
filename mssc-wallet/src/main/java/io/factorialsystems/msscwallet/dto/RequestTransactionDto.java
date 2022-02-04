package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransactionDto {
    private String userId;
    private Integer serviceId;
    private BigDecimal serviceCost;
    private String requestId;
    private String transactionDate;
    private String recipient;
}
