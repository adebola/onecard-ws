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
public class Account {
    private String id;
    private String name;
    private String userId;
    private Integer accountType;
    private Boolean activated;
    private BigDecimal balance;
    private Date createdDate;
    private String createdBy;
}
