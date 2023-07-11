package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceResponse {
    @JsonProperty("RESPONSE")
    private boolean response;

    @JsonProperty("RESPONSE_MSG")
    private String responseMessage;

    @JsonProperty("RESPONSE_CODE")
    private int ResponseCode;

    @JsonProperty("RESPONSE_DATA")
    public BalanceResponseData responseData;
}