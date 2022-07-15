package io.factorialsystems.msscpayments.domain;


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
public class RefundRequest {
    private String id;
    private String paymentId;
    private BigDecimal amount;
    private Timestamp refundedOn;
    private String refundedBy;
    private String fundRequestId;
}
