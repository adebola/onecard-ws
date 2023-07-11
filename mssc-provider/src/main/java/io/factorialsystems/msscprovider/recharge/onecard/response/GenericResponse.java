package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericResponse {
    @JsonProperty("RESPONSE")
    private boolean success;

    @JsonProperty("RESPONSE_MSG")
    private String message;

    @JsonProperty("RESPONSE_DATA")
    private String results;

    @JsonProperty("RESPONSE_CODE")
    private int ResponseCode;
}
