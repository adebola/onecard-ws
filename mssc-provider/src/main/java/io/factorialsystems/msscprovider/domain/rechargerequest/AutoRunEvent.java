package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoRunEvent {
    private String autoRequestId;
    private String title;
    private Integer recurringEventId;
    private String userId;
}
