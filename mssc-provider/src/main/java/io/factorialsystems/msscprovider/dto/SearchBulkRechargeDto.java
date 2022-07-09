package io.factorialsystems.msscprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SearchBulkRechargeDto {
    @NotBlank(message = "Id must be set")
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date searchDate;

    private String searchId;

    @NotNull(message = "PageNumber must be set")
    private Integer pageNumber;

    @NotNull(message = "pageSize must be set")
    private Integer pageSize;

}
