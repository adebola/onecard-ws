package io.factorialsystems.msscprovider.domain.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndividualRequestQuery {
    private Integer id;
    private String userId;
}
