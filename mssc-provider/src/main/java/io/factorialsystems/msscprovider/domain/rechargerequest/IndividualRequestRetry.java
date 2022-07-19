package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRequestRetry {
    private String id;
    private Integer requestId;
    private Timestamp retriedOn;
    private String retriedBy;
    private String recipient;
    private Boolean successful;
    private String statusMessage;
}
