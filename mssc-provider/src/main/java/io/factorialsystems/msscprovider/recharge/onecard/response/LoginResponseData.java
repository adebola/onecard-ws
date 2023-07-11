package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseData {
    @JsonProperty("USER_TOKEN")
    public String userToken;

    @JsonProperty("AUTH_TOKEN")
    public String authToken;

    @JsonProperty("EXPIRE_AT")
    public long expireAt;

    @JsonProperty("USER_ID")
    public UserID userId;
}
