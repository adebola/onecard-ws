package io.factorialsystems.msscusers.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PasswordDto {
    @NotEmpty
    private String password;
}
