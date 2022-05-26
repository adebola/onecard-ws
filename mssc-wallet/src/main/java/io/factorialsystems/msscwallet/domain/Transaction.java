package io.factorialsystems.msscwallet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Integer id;
    private String accountId;
    private String chargeAccountId;
    private Integer serviceId;
    private String serviceName;
    private String requestId;
    private Date txDate;
    private BigDecimal txAmount;
    private String recipient;
}
