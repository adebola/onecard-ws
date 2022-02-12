package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRechargeRequest  {
    private String id;
    private String requestId;
    private Integer requestType;
    private Timestamp scheduledDate;
    private Integer serviceId;
    private String serviceCode;
    private BigDecimal serviceCost;
    private BigDecimal totalServiceCost;
    private Integer groupId;
    private String[] recipients;
    private String recipient;
    private String productId;
    private String telephone;
    private String redirectUrl;
    private String authorizationUrl;
    private String paymentMode;
    private String paymentId;
    private Integer status;
    private String message;
    private Timestamp createdOn;
    private Timestamp ranOn;
    private Boolean closed;
}
