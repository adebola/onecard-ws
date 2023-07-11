package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class LoginResponse {

    @JsonProperty("RESPONSE")
    private boolean response;

    @JsonProperty("RESPONSE_MSG")
    private String responseMessage;

    @JsonProperty("RESPONSE_DATA")
    public LoginResponseData responseData;

    @JsonProperty("RESPONSE_CODE")
    private int responseCode;
}
