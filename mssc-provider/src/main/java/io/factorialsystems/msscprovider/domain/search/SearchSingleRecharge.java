package io.factorialsystems.msscprovider.domain.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSingleRecharge {
    private String userId;
    private String rechargeId;
    private Date startDate;
    private Date endDate;
    private String recipient;
    private String product;
    private Boolean failed;
    private Boolean unresolved;
}
