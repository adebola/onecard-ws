package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutoEventRan {
    private Integer periodId;
    private String autoRequestId;
    private Integer recurringEventId;
}
