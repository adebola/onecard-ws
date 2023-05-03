package io.factorialsystems.msscprovider.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CombinedRechargeRequest {
    private String id;
    private String serviceCode;
    private String productId;
    private BigDecimal serviceCost;
    private String recipient;
    private Timestamp createdAt;
    private Boolean failed;
    private String retryId;
    private String refundId;
    private String resolveId;
    private String rechargeType;
    private String results;
    private String userId;
    private String userName;
    private String parentId;
    private String paymentMode;
}
