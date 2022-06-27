package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoRechargeRequest {
    private String id;
    private String userId;
    private String title;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer recurringType;
    private Integer separationCount;
    private String paymentMode;
    private List<AutoIndividualRequest> recipients;
}
