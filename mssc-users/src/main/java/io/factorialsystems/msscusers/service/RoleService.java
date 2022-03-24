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

    public static final String ROLES_ONECARD = "Onecard";
    public static final String ROLES_COMPANY = "Company";

    @Autowired
    RoleService(Keycloak keycloak, KeycloakRoleMapper keycloakRoleMapper) {
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.rolesResource = keycloak.realm(ROLES_ONECARD).roles();
    }

    public List<KeycloakRoleDto> getRoles(){
        return getFilteredRoles(ROLES_ONECARD);
    }

    public List<KeycloakRoleDto> getCompanyRoles() {
        return getFilteredRoles(ROLES_COMPANY);
    }

    public KeycloakRoleDto getRoleById(String id) {

        return rolesResource.list()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }

    public KeycloakRoleDto getRoleByName(String name) {
        Optional<RoleRepresentation> foundRole = rolesResource.list()
                .stream()
                .filter(r -> r.getName().equals(name))
                .findFirst();

        return foundRole.map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }

    private List<KeycloakRoleDto> getFilteredRoles(String filter) {
        List<RoleRepresentation> roles  = rolesResource.list()
                .stream()
                .filter(r -> r.getName().startsWith(filter))
                .collect(Collectors.toList());

        return keycloakRoleMapper.listRoleRepresentationToDto(roles);
    }
}
