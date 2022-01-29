package io.factorialsystems.msscprovider.dto;

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
    String userId;
    Integer serviceId;
    BigDecimal serviceCost;
    String requestId;
    String transactionDate;
}
