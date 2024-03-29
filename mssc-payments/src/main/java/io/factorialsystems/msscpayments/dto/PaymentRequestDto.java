package io.factorialsystems.msscpayments.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @Min(1L)
    @NotNull(message = "Amount must be specified")
    BigDecimal amount;

    @Null(message = "Payment Id cannot be set")
    private String id;

    private Integer status;
    private String message;

    @Null(message = "callback_url cannot be set")
    private String authorizationUrl;

    private String redirectUrl;

    @Null(message = "status cannot be set")
    private Boolean verified;

    @NotNull(message = "Please select payment mode")
    private String paymentMode;

    @Null
    private BigDecimal balance;
}
