package io.factorialsystems.msscpayments.payment.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializeWalletTransactionRequest {
    private BigDecimal amount;
    private String requestId;
}
