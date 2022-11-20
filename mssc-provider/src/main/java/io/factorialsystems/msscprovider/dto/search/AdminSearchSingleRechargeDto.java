package io.factorialsystems.msscprovider.dto.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.factorialsystems.msscprovider.domain.search.SearchSingleRecharge;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class AdminSearchSingleRechargeDto {
    @NotBlank(message = "Id must be set")
    private String userId;
    private String rechargeId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date endDate;
    private String recipient;
    private String product;
    private Boolean failed;
    private Boolean unresolved;

    public SearchSingleRecharge toSearchSingle() {
        return SearchSingleRecharge.builder()
                .userId(userId)
                .rechargeId(rechargeId)
                .startDate(startDate)
                .endDate(endDate)
                .recipient(recipient)
                .product(product)
                .failed(failed)
                .unresolved(unresolved)
                .build();
    }
}
