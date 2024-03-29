package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirtimeRechargeResponse {
    @JsonProperty("RESPONSE")
    private boolean success;

    @JsonProperty("RESPONSE_MSG")
    private String message;

    @JsonProperty("RESPONSE_DATA")
    private List<AirtimeRechargeResponseData> data;
}
