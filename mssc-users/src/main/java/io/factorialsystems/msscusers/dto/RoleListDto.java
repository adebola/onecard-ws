package io.factorialsystems.msscusers.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoleListDto {

    @NotNull
    private String[] roleList;
}
