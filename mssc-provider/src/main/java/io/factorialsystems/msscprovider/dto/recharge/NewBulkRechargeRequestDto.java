package io.factorialsystems.msscprovider.dto.recharge;

import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeRequestDto {

    @Null(message = "id must be null")
    private String id;

    private String paymentMode;
    private String redirectUrl;

    @Null(message = "total service cost cannot be set it is computed")
    private BigDecimal totalServiceCost;

    @Null(message = "date cannot be set")
    private Date createdAt;

    @Null(message = "AutoRequestId cannot be set")
    private String autoRequestId;

    @NotEmpty
    private List<@Valid IndividualRequestDto> recipients;
}
