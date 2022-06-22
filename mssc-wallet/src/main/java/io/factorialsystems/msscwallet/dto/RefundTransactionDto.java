package io.factorialsystems.msscwallet.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RefundTransactionDto {

    @NotNull(message = "PaymentId must be specified")
    private String paymentId;

    @NotNull(message = "Amount to be Refunded must be specified")
    private BigDecimal amount;
}
