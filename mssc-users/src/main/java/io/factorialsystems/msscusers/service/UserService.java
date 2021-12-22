package io.factorialsystems.msscusers.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.mapper.KeycloakRoleMapper;
import io.factorialsystems.msscusers.mapper.KeycloakUserMapper;
import io.factorialsystems.msscusers.dto.KeycloakRoleDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.PagedDto;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UsersResource usersResource;
    private final RolesResource rolesResource;
    private final KeycloakUserMapper keycloakUserMapper;
    private final KeycloakRoleMapper keycloakRoleMapper;
    private final UserMapper userMapper;

    @Value("${keycloak.onecard}")
    private String onecardRealm;

    @Autowired
    UserService(Keycloak keycloak, KeycloakUserMapper keycloakUserMapper,
                KeycloakRoleMapper keycloakRoleMapper, UserMapper userMapper) {
        this.keycloakUserMapper = keycloakUserMapper;
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.usersResource =  keycloak.realm("onecard").users();
        this.rolesResource = keycloak.realm("onecard").roles();
        this.userMapper = userMapper;
    }

    public PagedDto<KeycloakUserDto> findUsers(Integer pageNumber, Integer pageSize ) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findAll());
    }

//    public List<KeycloakUserDto> findRealmUsers() {
//        return keycloakUserMapper.listUserRepresentationToDto(usersResource.list());
//    }

    public KeycloakUserDto findUserById(String id) {
        User user = userMapper.findUserById(id);
        return keycloakUserMapper.userToDto(user);
    }

    public KeycloakUserDto findRealmUserById(String id) {
        return keycloakUserMapper.userRepresentationToDto(getUserRepresentation(id));
    }

    public UserRepresentation getUserRepresentation(String id) {
        UserResource user = usersResource.get(id);

        if (user != null) {
            return user.toRepresentation();
        }

        return null;
    }

    public PagedDto<KeycloakUserDto> searchUser(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.search(searchString));
    }

//    public List<KeycloakUserDto> searchUser(String search) {
//        List<UserRepresentation> users = usersResource.search(search);
//
//        if (users != null && users.size() > 0) {
//            return keycloakUserMapper.listUserRepresentationToDto(users);
//        }
//
//        return null;
//    }

    public void updateUser(String id, KeycloakUserDto dto) {

        UserResource user = usersResource.get(id);

        if (user != null) {
            UserRepresentation representation = new UserRepresentation();

            representation.setFirstName(dto.getFirstName() == null ? null : dto.getFirstName());
            representation.setLastName(dto.getLastName() == null ? null : dto.getLastName());
            representation.setEnabled(dto.getEnabled());

           user.update(representation);
        }
    }

    public void changePassword(String id, String newPassword) {

        UserResource user = usersResource.get(id);

        if (user != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            user.resetPassword(credential);
        }
    }

    public List<String> getStringUserRoles(String id) {
        List<RoleRepresentation> roles = getUserRoleRepresentations(id);

        if (roles != null) {
            return roles.stream()
                    .map(r -> "ROLE_" + r.getName())
                    .collect(Collectors.toList());
        }

        return null;
    }

    public List<KeycloakRoleDto> getUserRoles(String id) {
        List<RoleRepresentation> roles = getUserRoleRepresentations(id);

        if (roles != null) {
            return keycloakRoleMapper.listRoleRepresentationToDto(roles);
        }

        return null;
    }

    private List<RoleRepresentation> getUserRoleRepresentations(String id) {
        UserResource user = usersResource.get(id);

        if (user != null) {
            List<RoleRepresentation> roles = user.roles()
                    .getAll()
                    .getRealmMappings();

            roles.removeIf(r -> !(r.getName().startsWith("Onecard")));

           return roles;
        }

        return null;
    }

    public void removeRoles(String id, String[] roleIds) {
        UserResource user = usersResource.get(id);

        if (user != null && roleIds != null && roleIds.length > 0) {
            List<RoleRepresentation> assignedRoles = user.roles()
                    .getAll()
                    .getRealmMappings();

            assignedRoles.removeIf(r -> {
                for (String roleId: roleIds) {
                    if (roleId.equals(r.getId())) {
                        return false;
                    }
                }

                return true;
            });


            user.roles()
                    .realmLevel()
                    .remove(assignedRoles);
        }
    }

    public void addRoles(String id, String[] roleIds) {
        UserResource user = usersResource.get(id);

        if (user != null && roleIds != null && roleIds.length > 0) {
            List<RoleRepresentation> roles = rolesResource.list();

            roles.removeIf(r -> {
                for (String roleId: roleIds) {
                    if (roleId.equals(r.getId())) {
                        return false;
                    }
                }

                return true;
            });

            user.roles()
                    .realmLevel()
                    .add(roles);
        }
    }

    private PagedDto<KeycloakUserDto> createDto(Page<User> users) {
        PagedDto<KeycloakUserDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) users.getTotal());
        pagedDto.setPageNumber(users.getPageNum());
        pagedDto.setPageSize(users.getPageSize());
        pagedDto.setPages(users.getPages());
        pagedDto.setList(keycloakUserMapper.listUserToDto(users.getResult()));
        return pagedDto;
    }
}
