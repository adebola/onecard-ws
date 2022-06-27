package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeRequest {
    private String id;
    private String userId;
    private String redirectUrl;
    private BigDecimal totalServiceCost;
    private String authorizationUrl;
    private String paymentMode;
    private String paymentId;
    private Boolean closed;
    private Boolean running;
    private String autoRequestId;
    private String scheduledRequestId;
    private String emailId;
    private Timestamp createdAt;
    private List<IndividualRequest> recipients;
}
