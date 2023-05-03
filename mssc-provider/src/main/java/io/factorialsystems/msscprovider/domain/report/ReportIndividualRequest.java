package io.factorialsystems.msscprovider.domain.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportIndividualRequest {
    private Integer id;
    private Integer serviceId;
    private String serviceCode;
    private String externalRequestId;
    private String bulkRequestId;
    private String scheduledRequestId;
    private String autoRequestId;
    private String productId;
    private BigDecimal serviceCost;
    private String telephone;
    private String recipient;
    private Boolean failed;
    private String failedMessage;
    private String refundId;
    private String retryId;
    private String resolveId;
    private String results;
    private String userId;
    private Date createdAt;
    private String parentId;
    private String paymentMode;
}
