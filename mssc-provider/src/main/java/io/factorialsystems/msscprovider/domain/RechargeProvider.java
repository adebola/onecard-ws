package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProvider {
    private Integer id;

    @EqualsAndHashCode.Exclude
    private String name;

    @EqualsAndHashCode.Exclude
    private String code;

    @EqualsAndHashCode.Exclude
    private String walletId;

    @EqualsAndHashCode.Exclude
    private String createdBy;

    @EqualsAndHashCode.Exclude
    private Timestamp createdDate;

    @EqualsAndHashCode.Exclude
    private Boolean activated;

    @EqualsAndHashCode.Exclude
    private String activatedBy;

    @EqualsAndHashCode.Exclude
    private Timestamp activationDate;

    @EqualsAndHashCode.Exclude
    private Boolean suspended;
}
