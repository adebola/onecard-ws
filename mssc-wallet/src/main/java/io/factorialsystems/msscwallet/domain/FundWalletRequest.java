package io.factorialsystems.msscwallet.domain;

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
public class FundWalletRequest {
    private String id;
    private String userId;
    private BigDecimal amount;
    private String authorizationUrl;
    private String redirectUrl;
    private Integer status;
    private String message;
    private String paymentId;
    private Boolean paymentVerified;
    private Timestamp createdOn;
    private Boolean closed;
    private Integer fundType;
}
