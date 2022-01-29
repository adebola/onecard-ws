package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransactionDto {
    String userId;
    Integer serviceId;
    BigDecimal serviceCost;
    String requestId;
    String transactionDate;
}
