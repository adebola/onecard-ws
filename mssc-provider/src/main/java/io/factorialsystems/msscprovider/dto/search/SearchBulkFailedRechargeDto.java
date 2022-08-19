package io.factorialsystems.msscprovider.dto.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SearchBulkFailedRechargeDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date searchDate;
    private String searchId;
    private Boolean unresolved;
}
