package io.factorialsystems.msscusers.domain.search;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchUserDto {
    @NotBlank(message = "Please supply search string")
    private String search;

    private Boolean admin;
    private Boolean ordinary;
}
