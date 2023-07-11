package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private Integer id;

    @EqualsAndHashCode.Exclude
    private Integer categoryId;

    @EqualsAndHashCode.Exclude
    private String category;

    @EqualsAndHashCode.Exclude
    private String name;

    @EqualsAndHashCode.Exclude
    private String code;

    @EqualsAndHashCode.Exclude
    private Boolean activated;

    @EqualsAndHashCode.Exclude
    private String activatedBy;

    @EqualsAndHashCode.Exclude
    private String createdBy;

    @EqualsAndHashCode.Exclude
    private Boolean suspended;

    @EqualsAndHashCode.Exclude
    private Timestamp createdDate;

    @EqualsAndHashCode.Exclude
    private Timestamp activationDate;
}
