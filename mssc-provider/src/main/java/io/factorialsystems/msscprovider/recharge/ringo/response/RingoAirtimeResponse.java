package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoAirtimeResponse {
    private String message;
    private Integer status;
    private BigDecimal amount;
    private BigDecimal amountCharged;
    private String network;
    private String msisdn;
    private String TransRef;
}
