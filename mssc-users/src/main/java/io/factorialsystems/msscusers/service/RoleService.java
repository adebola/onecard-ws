package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dto.KeycloakRoleDto;
import io.factorialsystems.msscusers.mapper.KeycloakRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {
    private final RolesResource rolesResource;
    private final KeycloakRoleMapper keycloakRoleMapper;

    @Autowired
    RoleService(Keycloak keycloak, KeycloakRoleMapper keycloakRoleMapper) {
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.rolesResource = keycloak.realm("onecard").roles();
    }

    public List<KeycloakRoleDto> getRoles(){
        List<RoleRepresentation> roles  = rolesResource.list()
                .stream()
                .filter(r -> r.getName().startsWith("Onecard"))
                .collect(Collectors.toList());

//        roles.removeIf(r -> !(r.getName().startsWith("Onecard")));
        log.info(String.format("Roles Size is %d", roles.size()));
        return keycloakRoleMapper.listRoleRepresentationToDto(roles);
    }

    public KeycloakRoleDto getRoleById(String id) {

        return rolesResource.list()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);

//        Optional<RoleRepresentation> foundRole = rolesResource.list()
//                .stream()
//                .filter(r -> r.getId().equals(id))
//                .findFirst()
//                .map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
//
//        return foundRole.map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }

    public KeycloakRoleDto getRoleByName(String name) {
        Optional<RoleRepresentation> foundRole = rolesResource.list()
                .stream()
                .filter(r -> r.getName().equals(name))
                .findFirst();

        return foundRole.map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }
}
