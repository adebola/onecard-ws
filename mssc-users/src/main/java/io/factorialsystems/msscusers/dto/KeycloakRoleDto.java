package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakRoleDto {

    @Null(message = "Role Id cannot be set")
    private String id;

    private String name;
    private String description;
}
