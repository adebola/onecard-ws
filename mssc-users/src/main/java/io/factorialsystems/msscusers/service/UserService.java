package io.factorialsystems.msscusers.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.*;
import io.factorialsystems.msscusers.exceptions.ResourceNotFoundException;
import io.factorialsystems.msscusers.mapper.KeycloakRoleMapper;
import io.factorialsystems.msscusers.mapper.KeycloakUserMapper;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UsersResource usersResource;
    private final RolesResource rolesResource;
    private final KeycloakUserMapper keycloakUserMapper;
    private final KeycloakRoleMapper keycloakRoleMapper;
    private final UserMapper userMapper;
    private final MailService mailService;

    @Value("${keycloak.onecard}")
    private String onecardRealm;

    @Value("${api.host.baseurl}")
    private String url;

    @Autowired
    UserService(Keycloak keycloak, KeycloakUserMapper keycloakUserMapper,
                KeycloakRoleMapper keycloakRoleMapper, UserMapper userMapper, MailService mailService) {

        this.keycloakUserMapper = keycloakUserMapper;
        this.keycloakRoleMapper = keycloakRoleMapper;
        this.usersResource = keycloak.realm("onecard").users();
        this.rolesResource = keycloak.realm("onecard").roles();
        this.userMapper = userMapper;
        this.mailService = mailService;
    }

    public PagedDto<KeycloakUserDto> findUsers(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.findAll());
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

    public SimpleUserDto verifyUser(String id) {
        return findSimpleUserById(id);
    }

    public PagedDto<KeycloakUserDto> searchUser(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(userMapper.search(searchString));
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

                final String userString = u.getUsername().equals(K.getUserName()) ? "You" : "Onecard";

                MailMessageDto mailMessageDto = MailMessageDto.builder()
                        .subject("User Profile Changed")
                        .to(u.getEmail())
                        .body(String.format("Your Profile has successfully been changed by %s", userString))
                        .build();

                mailService.sendMailWithOutAttachment(mailMessageDto);
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

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .subject("User Password Changed")
                    .to(user.toRepresentation().getEmail())
                    .body("Your Password has been successfully changed")
                    .build();

            mailService.sendMailWithOutAttachment(mailMessageDto);
        }
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

    public String sendLoginMessage(String id) {
        User user = Optional.ofNullable(userMapper.findUserById(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        final String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
        final String message = String.format("You logged on to Onecard at %s", date);

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .subject("Onecard Login Alert")
                .to(user.getEmail())
                .body(message)
                .build();

        return mailService.sendMailWithOutAttachment(mailMessageDto);
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
                    .getRealmMappings();

            assignedRoles.removeIf(r -> {
                for (String roleId : roleIds) {
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
                for (String roleId : roleIds) {
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
            final String fileName = "./" + file.getOriginalFilename();
            log.info(String.format("Received File %s Sending to UploadServer", fileName));

            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        new FileOutputStream(new File(fileName))
                );

                bufferedOutputStream.write(bytes);
                bufferedOutputStream.close();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//                headers.setBearerAuth(Objects.requireNonNull(K.getAccessToken()));

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new FileSystemResource(fileName));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                RestTemplate restTemplate = new RestTemplate();
                // restTemplate.getInterceptors().add(new RestTemplateInterceptor());

                return restTemplate.postForObject(url + "api/v1/upload2", requestEntity, String.class);

            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
        }

        throw new RuntimeException("Error Uploading File");
    }

    private String generateString() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

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
