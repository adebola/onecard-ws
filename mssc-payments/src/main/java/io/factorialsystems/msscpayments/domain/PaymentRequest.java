package io.factorialsystems.msscpayments.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String id;
    private BigDecimal amount;
    private Integer status;
    private String message;
    private String authorizationUrl;
    private String redirectUrl;
    private String accessCode;
    private String reference;
    private String paymentMode;
    private Boolean verified;
    private Timestamp paymentCreated;
    private Timestamp paymentVerified;
    private Boolean reversed;
}
