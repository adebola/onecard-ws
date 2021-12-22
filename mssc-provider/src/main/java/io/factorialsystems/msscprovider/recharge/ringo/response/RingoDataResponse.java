package io.factorialsystems.msscprovider.recharge.ringo.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoDataResponse {
    private String message;
    private String status;

    @JsonProperty("package")
    private String packageName;

    private BigDecimal amountCharged;
    private String network;
    private String msisdn;
    private String TransRef;

}
