package io.factorialsystems.msscprovider.domain.rechargerequest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleRechargeRequest {
    private String id;
    private String userId;
    private Integer serviceId;
    private String serviceCode;
    private String recipient;
    private String telephone;
    private String productId;
    private BigDecimal serviceCost;
    private String paymentId;
    private String authorizationUrl;
    private String redirectUrl;
    private Timestamp createdDate;
    private Boolean closed;
    private String paymentMode;
    private Integer status;
    private String message;
    private String accountType;
    private String autoRequestId;
    private String scheduledRequestId;
    private String bulkRequestId;
    private Boolean asyncRequest;
    private String name;
    private Timestamp createdAt;
}
