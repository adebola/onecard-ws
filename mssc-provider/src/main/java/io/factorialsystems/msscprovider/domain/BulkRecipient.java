package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkRecipient {
    private String bulkRequestId;
    private String msisdn;
}
