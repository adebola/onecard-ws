package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnecardMessageDto {

    @JsonProperty("RESPONSE_CODE")
    private String code;

    @JsonProperty("RESPONSE_MSG")
    private String message;
}
