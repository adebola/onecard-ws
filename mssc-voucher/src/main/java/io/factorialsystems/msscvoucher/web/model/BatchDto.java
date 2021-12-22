package io.factorialsystems.msscvoucher.web.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDto {

    @Null(message = "id cannot be set it will be automatically generated")
    private String id;

    @NotNull(message = "clusterId cannot be NULL")
    private String clusterId;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Null(message = "created Date cannot be set it will be automatically be generated")
    private Date createdDate;

    @Null(message = "Created By cannot be set")
    private String createdBy;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal denomination;

    @Min(value = 1, message = "Minimum Number of Vouchers generated cannot be less than 1")
    @NotNull(message = "Please specify the number of vouchers to generate count cannot be zero or null")
    private Integer voucherCount;

    private Boolean activated;

    @Null(message = "Activation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date activationDate;

    @Null(message = "Activated By cannot be set")
    private String activatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date expiryDate;

    private Boolean suspended;
}
