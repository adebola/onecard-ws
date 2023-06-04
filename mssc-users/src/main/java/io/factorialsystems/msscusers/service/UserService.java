package io.factorialsystems.msscusers.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.domain.search.SearchUserDto;
import io.factorialsystems.msscusers.dto.*;
import io.factorialsystems.msscusers.exceptions.ResourceNotFoundException;
import io.factorialsystems.msscusers.external.client.CommunicationClient;
import io.factorialsystems.msscusers.mapper.KeycloakRoleMapper;
import io.factorialsystems.msscusers.mapper.KeycloakUserMapper;
import io.factorialsystems.msscusers.mapper.dbtransfer.RoleParameter;
import io.factorialsystems.msscusers.utils.K;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
    private static final String MAIL_PROFILE_HEADER = "Dear %s %s\n\nYour Profile has successfully been changed";
    private static final String MAIL_PASSWORD_CHANGE_HEADER = "Dear %s %s\n\nYou have successfully changed your password";
    private static final String MAIL_LOGIN_HEADER = "Dear %s %s\n\nYou have successfully logged on to your Onecard Recharge Suite.\n%s";
    private final UsersResource usersResource;
    private final RolesResource rolesResource;
    private final KeycloakUserMapper keycloakUserMapper;
    private final KeycloakRoleMapper keycloakRoleMapper;
    private final UserMapper userMapper;
    private final CommunicationClient communicationClient;

    @Value("${keycloak.onecard}")
    private String onecardRealm;

    @Value("${api.host.baseurl}")
    private String url;

    @Value("${realm.admin.id}")
    private String realmAdminId;

    @Autowired
    UserService(Keycloak keycloak, KeycloakUserMapper keycloakUserMapper,
                KeycloakRoleMapper keycloakRoleMapper, UserMapper userMapper, CommunicationClient communicationClient) {

        this.keycloakUserMapper = keycloakUserMapper;
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.usersResource = keycloak.realm("onecard").users();
        this.rolesResource = keycloak.realm("onecard").roles();
        this.userMapper = userMapper;
        this.communicationClient = communicationClient;
    }

    public PagedDto<KeycloakUserDto> findUsers(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findAll());
    }


    public PagedDto<KeycloakUserDto> findAdminUsers(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findAdminUser());
    }


    public PagedDto<KeycloakUserDto> findOrdinaryUsers(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findOrdinaryUser());
    }

    public KeycloakUserDto findUserById(String id) {
        User user = userMapper.findUserById(id);
        return keycloakUserMapper.userToDto(user);
    }

    public SimpleUserDto findSimpleUserById(String id) {
        User user = userMapper.findUserById(id);
        return keycloakUserMapper.userToSimpleDto(user);
    }

    public SimpleUserDto findSimpleUserByIdOrNameOrEmail(String string) {
        User user = userMapper.findUserByIdOrNameOrEmail(string);
        return keycloakUserMapper.userToSimpleDto(user);
    }

    public List<SimpleUserDto> findAllSimpleUsers() {
        List<User> users = userMapper.findAllList();

        return users.stream().map(keycloakUserMapper::userToSimpleDto)
                .collect(Collectors.toList());
    }

    public SimpleUserDto verifyUser(String id) {
        return findSimpleUserById(id);
    }

    public PagedDto<KeycloakUserDto> searchUser(Integer pageNumber, Integer pageSize, SearchUserDto dto) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.search(dto));
    }

    public void updateUser(String id, KeycloakUserDto dto) {
        boolean changed = false;

        UserResource user = usersResource.get(id);
        User u = keycloakUserMapper.userDtoToUser(dto);

        if (user != null) {
            UserRepresentation representation = new UserRepresentation();

            if (dto.getFirstName() != null) {
                changed = true;
                representation.setFirstName(dto.getFirstName());
                u.setFirstName(dto.getFirstName());
            }

            if (dto.getLastName() != null) {
                changed = true;
                representation.setLastName(dto.getLastName());
                u.setLastName(dto.getLastName());
            }

            if (changed) {
                u.setId(id);
                user.update(representation);
                userMapper.update(u);

                final String message = String.format(MAIL_PROFILE_HEADER,
                        representation.getFirstName(), representation.getLastName());

                MailMessageDto mailMessageDto = MailMessageDto.builder()
                        .subject("User Profile Changed")
                        .to(user.toRepresentation().getEmail())
                        .body(message)
                        .build();

                communicationClient.sendMailWithoutAttachment(mailMessageDto);
            }
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
            UserRepresentation userRepresentation = user.toRepresentation();

            final String message = String.format(MAIL_PASSWORD_CHANGE_HEADER,
                    userRepresentation.getFirstName(), userRepresentation.getLastName());

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .subject("User Password Changed")
                    .to(userRepresentation.getEmail())
                    .body(message)
                    .build();

            communicationClient.sendMailWithoutAttachment(mailMessageDto);
        }
    }

    public boolean toggleUser(String id) {
        UserResource userResource = usersResource.get(id);

        if (userResource != null) {
            UserRepresentation representation = userResource.toRepresentation();
            boolean status = representation.isEnabled();
            representation.setEnabled(!status);
            userResource.update(representation);

            User user = userMapper.findUserById(id);
            user.setEnabled(!status);
            userMapper.update(user);

            return !status;
        }

        return false;
    }

    public List<String> getStringUserRoles(String id) {
        List<RoleRepresentation> roles = getUserRoleRepresentations(id, K.ROLES_ONECARD);

        if (roles != null) {
            return roles.stream()
                    .map(r -> "ROLE_" + r.getName())
                    .collect(Collectors.toList());
        }

        return null;
    }

    public List<KeycloakRoleDto> getUserRoles(String id) {
        List<RoleRepresentation> roles = getUserRoleRepresentations(id, K.ROLES_ONECARD);

        if (roles != null) {
            return keycloakRoleMapper.listRoleRepresentationToDto(roles);
        }

        return null;
    }

    public List<KeycloakRoleDto> getUserAssignedCompanyRoles(String id) {
        List<RoleRepresentation> roles = getUserRoleRepresentations(id, K.ROLES_COMPANY);

        if (roles != null) {
            return keycloakRoleMapper.listRoleRepresentationToDto(roles);
        }

        return null;
    }

    public void sendLoginMessage(String id) {

       if (id.equals(realmAdminId)) {
           log.info("Retrieving Realm Admin Id {}", id);
           return;
       }

        User user = userMapper.findUserById(id);

        if (user == null) {
            log.error("User with id {} not found", id);
            throw new ResourceNotFoundException("User", "id", id);
        }

        final String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());

        final String message = String.format(MAIL_LOGIN_HEADER,
                user.getFirstName(), user.getLastName(), date);

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .subject("Onecard Login Alert")
                .to(user.getEmail())
                .body(message)
                .build();

        communicationClient.sendMailWithoutAttachment(mailMessageDto);
    }

    public List<KeycloakRoleDto> getUserAssignableCompanyRoles(String id) {
        List<KeycloakRoleDto> userRoles = getUserAssignedCompanyRoles(id);

        return rolesResource.list().stream()
                .filter(r -> r.getName().startsWith(K.ROLES_COMPANY))
                .filter(r -> userRoles.stream().noneMatch(u -> u.getName().equals(r.getName())))
                .map(keycloakRoleMapper::roleRepresentationToDto)
                .collect(Collectors.toList());
    }

    private List<RoleRepresentation> getUserRoleRepresentations(String id, String roleName) {
        UserResource user = usersResource.get(id);

        if (user != null) {
            List<RoleRepresentation> roles = user.roles()
                    .getAll()
                    .getRealmMappings();

            return roles.stream()
                    .filter(r -> r.getName().startsWith(roleName)).collect(Collectors.toList());
        }

        return null;
    }

    public void addCompanyRoles(String id, String[] roleIds) {
        User modUser = userMapper.findUserById(id);
        User adminUser = userMapper.findUserById(K.getUserId());

        if (modUser == null || adminUser == null || modUser.getOrganizationId() == null || adminUser.getOrganizationId() == null) {
            final String errorMessage = "Add Roles Error, Null User or Organization Id";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        if (modUser.getOrganizationId().equals(adminUser.getOrganizationId())) {
            addRoles(id, roleIds);
        } else {
            final String errorMessage
                    = String.format("Unable to Modify User Roles Admin OrganisationId (%s), Target OrganisationId  (%s)", adminUser.getOrganizationId(), modUser.getOrganizationId());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public void removeCompanyRoles(String id, String[] roleIds) {
        User modUser = userMapper.findUserById(id);
        User adminUser = userMapper.findUserById(K.getUserId());

        if (modUser == null || adminUser == null || modUser.getOrganizationId() == null || adminUser.getOrganizationId() == null) {
            final String errorMessage = "Remove Roles Error, Null User or Organization Id";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        if (modUser.getOrganizationId().equals(adminUser.getOrganizationId())) {
            UserResource user = usersResource.get(id);
            long roleCount = user.roles().getAll().getRealmMappings().stream()
                    .filter(r -> r.getName().startsWith("ROLE_Company"))
                    .count();

            if (roleCount == roleIds.length && roleIds.length > 0) {
                throw new RuntimeException("You cannot remove all Company Roles from a Company User");
            }

            removeRoles(id, roleIds);
        } else {
            final String errorMessage
                    = String.format("Unable to Modify User Roles Admin OrganisationId (%s), Target OrganisationId  (%s)", adminUser.getOrganizationId(), modUser.getOrganizationId());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public void removeRoles(String id, String[] roleIds) {
        UserResource user = usersResource.get(id);

        if (user != null && roleIds != null && roleIds.length > 0) {
            List<RoleRepresentation> assignedRoles = user.roles()
                    .getAll()
                    .getRealmMappings().stream()
                    .filter(r -> Arrays.stream(roleIds).anyMatch(i -> i.equals(r.getId())))
                    .collect(Collectors.toList());

            user.roles().realmLevel().remove(assignedRoles);

            Arrays.stream(roleIds).forEach(r -> {
                userMapper.removeRole(
                        RoleParameter.builder()
                                .roleId(r)
                                .userId(id)
                                .build()
                );
            });
        }
    }

    public void addRoles(String id, String[] roleIds) {
        UserResource user = usersResource.get(id);

        if (user != null && roleIds != null && roleIds.length > 0) {
            List<RoleRepresentation> roles = rolesResource.list().stream()
                    .filter(r -> Arrays.stream(roleIds).anyMatch(i -> i.equals(r.getId())))
                    .collect(Collectors.toList());

            user.roles().realmLevel().add(roles);

            // Local Database Implementation of Roles
            List<RoleParameter> parameters = Arrays.stream(roleIds)
                    .map(r -> RoleParameter.builder().roleId(r).userId(id).build())
                    .collect(Collectors.toList());

            userMapper.addRoles(parameters);
        }
    }

    public UserEntryListDto getUserNameListFromIds(UserIdListDto dto) {
        return new UserEntryListDto(userMapper.getUserNamesFromIds(dto.getEntries()));
    }

    public PagedDto<KeycloakUserDto> findUserByOrganizationId(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findUserByOrganizationId(id));
    }

    public PagedDto<KeycloakUserDto> findUserForOrganization(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findUserForOrganization());
    }

    public UserSecretDto generateSecret() {
        String id = K.getUserId();

        if (id != null) {
            User user = userMapper.findUserById(id);

            if (user == null) {
                throw new RuntimeException(String.format("Unable to Load Current User (%s)", id));
            }

            final String secret = generateString();

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setSecret(encoder.encode(secret));
            userMapper.update(user);

            return UserSecretDto.builder()
                    .secret(secret)
                    .build();
        }

        return null;
    }

    public String saveImageFile(MultipartFile file) {
        if (!file.isEmpty()) {
            return communicationClient.uploadFile(file);
        }

        return null;
    }

    private String generateString() {
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++)
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));

        return sb.toString();
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
