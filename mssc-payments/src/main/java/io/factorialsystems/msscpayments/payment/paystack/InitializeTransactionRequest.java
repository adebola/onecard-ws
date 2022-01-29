package io.factorialsystems.msscpayments.payment.paystack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitializeTransactionRequest {

    @Digits(integer = 9, fraction = 0)
    private Integer amount;

    @NotNull
    private String email;

    private String callback_url;
}
