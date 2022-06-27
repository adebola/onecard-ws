package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class IndividualRequestRetry {
    private Integer id;
    private Integer bulkIndividualRequestId;
    private Timestamp retriedOn;
    private Boolean successful;
    private String statusMessage;
    private String paymentId;
}
