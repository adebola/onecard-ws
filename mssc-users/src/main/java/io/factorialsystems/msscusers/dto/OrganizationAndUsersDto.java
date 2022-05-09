package io.factorialsystems.msscusers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrganizationAndUsersDto {
    private String id;
    private String organizationName;
    private List<KeycloakUserDto> users;
}
