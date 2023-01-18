package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dto.KeycloakRoleDto;
import io.factorialsystems.msscusers.mapper.KeycloakRoleMapper;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {
    private final RolesResource rolesResource;
    private final KeycloakRoleMapper keycloakRoleMapper;

    @Autowired
    RoleService(Keycloak keycloak, KeycloakRoleMapper keycloakRoleMapper) {
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.rolesResource = keycloak.realm(K.ROLES_ONECARD).roles();
    }

    public List<KeycloakRoleDto> getRoles(){
        return getFilteredRoles(K.ROLES_ONECARD);
    }

    public List<KeycloakRoleDto> getCompanyRoles() {
        return getFilteredRoles(K.ROLES_COMPANY);
    }

    public KeycloakRoleDto getRoleById(String id) {
        return rolesResource.list()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }

    public KeycloakRoleDto getRoleByName(String name) {
        return rolesResource.list()
                .stream()
                .filter(r -> r.getName().equals(name))
                .findFirst()
                .map(keycloakRoleMapper::roleRepresentationToDto).orElse(null);
    }

    private List<KeycloakRoleDto> getFilteredRoles(String filter) {
        return rolesResource.list().stream()
                .filter(r -> r.getName().startsWith(filter))
                .map(keycloakRoleMapper::roleRepresentationToDto)
                .collect(Collectors.toList());
    }
}
