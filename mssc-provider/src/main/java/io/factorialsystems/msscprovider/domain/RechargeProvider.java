package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProvider {
    private Integer id;
    private String name;
    private String code;
    private String walletId;
    private String createdBy;
    private Timestamp createdDate;
    private Boolean activated;
    private String activatedBy;
    private Timestamp activationDate;
    private Boolean suspended;
}
