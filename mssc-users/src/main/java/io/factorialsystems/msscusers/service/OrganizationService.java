package io.factorialsystems.msscusers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscusers.config.JMSConfig;
import io.factorialsystems.msscusers.dao.OrganizationMapper;
import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.*;
import io.factorialsystems.msscusers.external.client.AccountClient;
import io.factorialsystems.msscusers.mapper.OrganizationMapstructMapper;
import io.factorialsystems.msscusers.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.security.AccessControlException;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserService userService;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final AccountClient accountClient;
    private final OrganizationMapper organizationMapper;
    private final OrganizationMapstructMapper organizationMapstructMapper;

    private static final String COMPANY_USER_ROLE = "Company_User";
    private static final String COMPANY_ADMIN_ROLE = "Company_Admin";
    private static final String COMPANY_OPERATOR_ROLE = "Company_Operator";

    public PagedDto<OrganizationDto> findAll(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(organizationMapper.findAll());
    }

    public OrganizationDto findById(String id) {
        return organizationMapstructMapper.organizationToDto(organizationMapper.findById(id));
    }

    public PagedDto<OrganizationDto> search(String searchString, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(organizationMapper.search(searchString));
    }

    public OrganizationDto save(OrganizationDto dto) {
        final String id = UUID.randomUUID().toString();

        Organization organization = organizationMapstructMapper.dtoToOrganization(dto);

        CreateAccountDto accountDto = CreateAccountDto.builder()
                .userName(dto.getOrganizationName())
                .userId(id) // Dummy User id, Organizations are not users
                .accountType(K.ACCOUNT_TYPE_CORPORATE)
                .build();

        AccountDto newAccount = accountClient.createAccount(accountDto);

        if (newAccount == null || newAccount.getId() == null) {
            String message = String.format("Error creating Account for Organization (%s)", dto.getOrganizationName());
            log.error(message);
            throw new RuntimeException(message);
        }

        organization.setId(id);
        organization.setWalletId(newAccount.getId());
        organization.setCreatedBy(K.getUserName());
        organizationMapper.save(organization);
        dto.setId(id);

        return dto;
    }

    public OrganizationDto update(String id, OrganizationDto dto) {
        Organization organization = organizationMapstructMapper.dtoToOrganization(dto);
        organization.setId(id);
        organizationMapper.update(organization);
        return organizationMapstructMapper.organizationToDto(organizationMapper.findById(id));
    }

    @SneakyThrows
    public void delete(String id) {
        Organization organization = organizationMapper.findById(id);

        if (organization == null) {
            log.error("Organization {} not Available for deletion", id);
            return;
        }

        Integer count = organizationMapper.findUserCount(id);

        if (count != null && count == 0) {
            log.info("Deleting Organization {}", organization.getId());
            organizationMapper.delete(id);

            DeleteAccountDto dto = DeleteAccountDto.builder()
                    .id(organization.getWalletId())
                    .deletedBy(K.getPreferredUserName())
                    .build();

            // Delete the account
            jmsTemplate.convertAndSend(JMSConfig.DELETE_ACCOUNT_QUEUE, objectMapper.writeValueAsString(dto));
        }

        throw new RuntimeException("Organization cannot be deleted, it has associated Users");
    }

    public void addUserToOrganization(String id, List<String> userIds) {
        KeycloakRoleDto companyRole = roleService.getRoleByName(COMPANY_ADMIN_ROLE);

        if (companyRole == null) {
            throw new RuntimeException("Unable to get Company Roles, Roles may not have been created");
        }

        String[] roleArray = new String[1];
        roleArray[0] = companyRole.getId();

        userIds.forEach(userId -> {
            User user = userMapper.findUserById(userId);

            if (user == null) {
                throw new RuntimeException(String.format("Unable to Add User %s not Found", userId));
            }

            List<KeycloakRoleDto> roles = userService.getUserRoles(userId);

            if (roles != null && !roles.isEmpty()) {
                if (roles.stream().anyMatch(r -> r.getName().startsWith(K.ROLES_ONECARD))) {
                    throw new RuntimeException(String.format("User %s is a Onecard User / Administrator and cannot be added to an organization", user.getEmail()));
                }
            }

            if (user.getOrganizationId() != null || roles.stream().anyMatch(r -> r.getName().startsWith(K.ROLES_COMPANY))) {
                throw new RuntimeException(String.format("User %s belongs to an organization", user.getEmail()));
            }

            // Add Organization to User and Save
            user.setOrganizationId(id);

            userMapper.update(user);
            userService.addRoles(userId, roleArray);

            UserOrganizationAmendDto dto = UserOrganizationAmendDto.builder()
                    .userId(userId)
                    .organizationId(id)
                    .build();

            try {
                jmsTemplate.convertAndSend(JMSConfig.ADD_ORGANIZATION_ACCOUNT_QUEUE, objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                final String errorMessage = "Error Amending User Account : " + e.getMessage();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        });
    }

    public void removeUserFromOrganization(String id, List<String> userIds) {

        User currentUser = null;
        List<String> grantedRoles = K.getRoles();

        if (grantedRoles != null && grantedRoles.stream().anyMatch(r -> r.startsWith("Company"))) {
            currentUser = userMapper.findUserById(K.getUserId());
        }

        KeycloakRoleDto userRole = roleService.getRoleByName(COMPANY_USER_ROLE);
        KeycloakRoleDto operatorRole = roleService.getRoleByName(COMPANY_OPERATOR_ROLE);
        KeycloakRoleDto companyRole = roleService.getRoleByName(COMPANY_ADMIN_ROLE);

        if (companyRole == null) {
            throw new RuntimeException("Unable to get Company Roles, Roles may not have been created");
        }

        String[] roleArray = new String[3];
        roleArray[0] = companyRole.getId();
        roleArray[1] = operatorRole.getId();
        roleArray[2] = userRole.getId();

        final User finalCurrentUser = currentUser;

        userIds.forEach(userId -> {
            User user = userMapper.findUserById(userId);

            if (user == null) {
                throw new RuntimeException(String.format("Unable to Add User %s not Found", userId));
            }

            if (K.isAdmin() || (finalCurrentUser != null && !finalCurrentUser.getOrganizationId().equals(user.getOrganizationId()))) {
                userMapper.removeOrganization(user.getId());
                userService.removeRoles(userId, roleArray);
            } else {
                throw new AccessControlException("Remove User Error");
            }

            UserOrganizationAmendDto dto = UserOrganizationAmendDto.builder()
                    .userId(userId)
                    .organizationId(id)
                    .build();

            try {
                jmsTemplate.convertAndSend(JMSConfig.REMOVE_ORGANIZATION_ACCOUNT_QUEUE, objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                final String errorMessage = "Error Removing Organization from User Account : " + e.getMessage();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        });
    }

    public OrganizationAndUsersDto getOrganizationAndUsers(String id) {
        Organization organization = organizationMapper.findById(id);
        PagedDto<KeycloakUserDto> users = userService.findUserByOrganizationId(id, 1, 50);

        OrganizationAndUsersDto dto = OrganizationAndUsersDto.builder()
                .organizationName(organization.getOrganizationName())
                .id(organization.getId())
                .users(users.getList())
                .build();

        if (K.isAdmin()) return dto;

        if (K.isCompanyAdmin()) {
            User user = userMapper.findUserById(K.getUserId());

            if (user.getOrganizationId().equals(id)) {
                return dto;
            }
        }

        throw new RuntimeException("Access denied to Resource");
    }

    private PagedDto<OrganizationDto> createDto(Page<Organization> organizations) {
        PagedDto<OrganizationDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) organizations.getTotal());
        pagedDto.setPageNumber(organizations.getPageNum());
        pagedDto.setPageSize(organizations.getPageSize());
        pagedDto.setPages(organizations.getPages());

        pagedDto.setList(organizationMapstructMapper.listOrganizationToDto(organizations.getResult()));
        return pagedDto;
    }
}
