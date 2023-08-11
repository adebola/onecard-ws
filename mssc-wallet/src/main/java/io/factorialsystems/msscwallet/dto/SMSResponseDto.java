package io.factorialsystems.msscwallet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SMSResponseDto {
    private Boolean status;
    private String message;
}
