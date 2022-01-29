package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRechargeRequest {
    private String id;
    private String requestId;
    private Integer requestType;
    private Timestamp scheduledDate;
    private String serviceCode;
    private Integer groupId;
    private String[] recipients;
    private String recipient;
    private String productId;
    private String telephone;
    private BigDecimal serviceCost;
    private String redirectUrl;
    private String paymentMode;
    private Timestamp createdOn;
    private Timestamp ranOn;
    private Boolean closed;
}
