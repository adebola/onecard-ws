package io.factorialsystems.msscpayments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    @Null(message = "Payment Id cannot be set")
    private String id;

    @NotNull(message = "Amount must be specified")
    BigDecimal amount;

    @Null(message = "callback_url cannot be set")
    private String authorizationUrl;

    private String redirectUrl;

    @Null(message = "status cannot be set")
    private Boolean verified;
}
