package io.factorialsystems.msscprovider.dto.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.Data;

import java.util.Date;

@Data
public class SearchSingleRechargeDto {
    private String rechargeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date endDate;

    private String recipient;
    private String product;

    private Boolean failed;

    public SearchSingleRecharge toSearchSingle() {
        return SearchSingleRecharge.builder()
                .userId(ProviderSecurity.getUserId())
                .rechargeId(rechargeId)
                .startDate(startDate)
                .endDate(endDate)
                .recipient(recipient)
                .product(product)
                .failed(failed)
                .build();
    }
}
