package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkRechargeRequest {
    private String id;
    private Integer serviceId;
    private String serviceCode;
    private Integer groupId;
    private String productId;
    private String redirectUrl;
    private BigDecimal serviceCost;
    private BigDecimal totalServiceCost;
    private String authorizationUrl;
    private String paymentMode;
    private String paymentId;
    private Boolean closed;
    private String[] recipients;
    private String autoRequestId;
    private String scheduledRequestId;
}
