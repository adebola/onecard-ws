package io.factorialsystems.msscprovider.dto.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SearchSingleFailedRechargeDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date searchDate;

    private String searchRecipient;
    private String searchProduct;
    private Boolean unresolved;
}
