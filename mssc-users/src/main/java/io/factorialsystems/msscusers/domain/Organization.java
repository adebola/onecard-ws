package io.factorialsystems.msscusers.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String id;
    private String organizationName;
    private String walletId;
    private String createdBy;
    private Timestamp createdDate;
}
