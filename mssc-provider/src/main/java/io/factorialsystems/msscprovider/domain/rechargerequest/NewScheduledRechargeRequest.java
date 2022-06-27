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
public class NewScheduledRechargeRequest {
    private String id;
    private String userId;
    private String userEmail;
    private Integer requestType;
    private Timestamp scheduledDate;
    private BigDecimal totalServiceCost;
    private String redirectUrl;
    private String authorizationUrl;
    private String paymentMode;
    private String paymentId;
    private Integer status;
    private String message;
    private Timestamp createdOn;
    private Timestamp ranOn;
    private Boolean closed;
    List<IndividualRequest> recipients;
}
