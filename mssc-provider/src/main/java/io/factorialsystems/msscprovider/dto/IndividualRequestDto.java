package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualRequestDto {
    @NotEmpty
    private String serviceCode;

    private String productId;

    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    private String telephone;

    @NotEmpty
    private String recipient;

    @Null(message = "Failed cannot be set")
    private Boolean failed;

    @Null(message = "Failure message cannot be set")
    private String failedMessage;

    @Null(message = "RefundId cannot be set")
    private String refundId;

    @Null(message = "Successful Retry Id cannot be set")
    private String retryId;
}
