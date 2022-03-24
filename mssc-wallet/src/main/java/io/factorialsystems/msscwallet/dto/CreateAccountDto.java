package io.factorialsystems.msscwallet.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CreateAccountDto {
    @NotEmpty(message = "UserId cannot be NULL or Empty")
    private String userId;

    @NotEmpty(message = "UserName cannot be NULL or Empty")
    private String userName;
    private String createdBy;

    @Digits(integer = 1, fraction = 0)
    private Integer accountType;
}
