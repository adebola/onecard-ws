package io.factorialsystems.msscwallet.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String id;
    private String name;
    private String userId;
    private Integer accountType;
    private Boolean activated;
    private BigDecimal balance;
    private Date createdDate;
    private String createdBy;
    private String chargeAccountId;
    private String webHook;
    private Boolean kycVerified;
    private BigDecimal dailyLimit;
}
