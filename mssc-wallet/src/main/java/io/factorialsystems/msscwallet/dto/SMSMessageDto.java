package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SMSMessageDto {
    private String message;
    private String to;
}
