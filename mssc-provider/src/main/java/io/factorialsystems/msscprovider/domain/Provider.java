package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private Integer id;
    private Integer categoryId;
    private String category;
    private String name;
    private String code;
    private Boolean activated;
    private String activatedBy;
    private String createdBy;
    private Boolean suspended;
    private Timestamp createdDate;
    private Timestamp activationDate;
}
