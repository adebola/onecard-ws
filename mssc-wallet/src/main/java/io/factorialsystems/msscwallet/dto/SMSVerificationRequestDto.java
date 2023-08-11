package io.factorialsystems.msscwallet.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SMSVerificationRequestDto {
    @NotEmpty
    private String id;

    @NotEmpty
    private String code;
}
