package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortAutoRechargeRequest {
    private String id;
    private String title;
    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp createdOn;
    private Integer recurringType;
}