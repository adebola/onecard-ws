package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundTransactionResponseDto {
    private String id;
    private Integer status;
    private String transactionId;
    private String  paymentId;
    private String message;
}
