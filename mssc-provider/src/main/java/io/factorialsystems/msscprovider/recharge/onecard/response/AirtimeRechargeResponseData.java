package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirtimeRechargeResponseData {
    private String product_id;
    private String mobile;
    private BigDecimal amount;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("TXN_ID")
    private String txId;
}