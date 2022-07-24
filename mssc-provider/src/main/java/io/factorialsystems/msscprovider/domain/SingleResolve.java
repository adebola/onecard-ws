package io.factorialsystems.msscprovider.domain;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SingleResolve {
    private String id;
    private String rechargeId;
    private String resolvedBy;
    private Timestamp resolvedOn;
    private String resolutionMessage;
}
