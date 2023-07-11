package io.factorialsystems.msscprovider.domain.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeReportRequest {
    private String userId;
    private Integer serviceId;
    private String type;
    private Boolean status;
    private Date startDate;
    private Date endDate;
    private Integer providerId;
}
