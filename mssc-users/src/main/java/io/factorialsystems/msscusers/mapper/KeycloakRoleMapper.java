package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.dto.KeycloakRoleDto;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper()
public interface KeycloakRoleMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "description", target = "description")
    })
    KeycloakRoleDto roleRepresentationToDto(RoleRepresentation roleRepresentation);
    List<KeycloakRoleDto> listRoleRepresentationToDto(List<RoleRepresentation> roleRepresentations);
}
