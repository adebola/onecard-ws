package io.factorialsystems.msscpayments.payment.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitializeWalletTransactionResponse {
    private Integer status;
    private String message;
    private BigDecimal balance;
}
