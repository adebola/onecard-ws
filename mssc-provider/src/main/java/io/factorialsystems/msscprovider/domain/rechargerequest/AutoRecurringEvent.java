package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoRecurringEvent {
    private Integer id;
    private String autoRequestId;
    private Integer dayOfPeriod;
    private boolean disabled;
}
