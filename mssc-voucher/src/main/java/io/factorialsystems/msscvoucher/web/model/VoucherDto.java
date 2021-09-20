package io.factorialsystems.msscvoucher.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDto {

    @Null(message = "id cannot be set it will be automatically generated")
    private Integer id;

    @Null(message = "voucher code cannot be set it will be automatically generated")
    private String code;

    @Null(message = "voucher serial_number cannot be set it will be automatically generated")
    private String serialNumber;

    private BigDecimal denomination;

    @Null(message = "created Date cannot be set it will be automatically be generated")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @Null (message = "createdBy cannot be set")
    private String createdBy;

    @Null(message = "batch_id cannot be set")
    private String batchId;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private Date expiryDate;
    private Boolean activated;
}
