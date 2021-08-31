package io.factorialsystems.msscvoucher.web.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDto {

    @Null(message = "id cannot be set it will be automatically generated")
    private String id;

    @Null(message = "created Date cannot be set it will be automatically be generated")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @Null
    private String createdBy;

    @NotNull
    private BigDecimal denomination;

    @NotNull(message = "Please specify the number of vouchers to generate count cannot be zero or null")
    private Integer count;

    private Boolean activated;

    @Null(message = "Activation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date activationDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date expiryDate;
}
