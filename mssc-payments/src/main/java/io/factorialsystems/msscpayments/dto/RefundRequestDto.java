package io.factorialsystems.msscpayments.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class RefundRequestDto {
    @NotBlank(message = "Please specify User to refund")
    private String userId;

    @Min(1L)
    @NotNull(message = "Amount to be Refunded must be specified")
    private BigDecimal amount;
}
