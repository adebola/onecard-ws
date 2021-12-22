package io.factorialsystems.msscprovider.recharge.jed.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String accessCode;
    private Integer amount;
    private String phone;
}
