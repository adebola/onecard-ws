package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.dao.OrganizationMapper;
import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.SimpleUserDto;
import io.factorialsystems.msscusers.external.client.AccountClient;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class KeycloakUserMapperDecorator implements KeycloakUserMapper {
    private AccountClient accountClient;
    private KeycloakUserMapper keycloakUserMapper;
    private OrganizationMapper organizationMapper;

    @Autowired
    public void setAccountClient(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Autowired
    public void setKeycloakUserMapper(KeycloakUserMapper keycloakUserMapper) {
        this.keycloakUserMapper = keycloakUserMapper;
    }

    @Override
    public KeycloakUserDto userRepresentationToDto(UserRepresentation userRepresentation) {
        return keycloakUserMapper.userRepresentationToDto(userRepresentation);
    }

    @Autowired
    public void setOrganizationMapper(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    @Override
    public List<KeycloakUserDto> listUserRepresentationToDto(List<UserRepresentation> userRepresentations) {
        return keycloakUserMapper.listUserRepresentationToDto(userRepresentations);
    }

    @Override
    public KeycloakUserDto userToDto(User user) {

        KeycloakUserDto dto = keycloakUserMapper.userToDto(user);

        String walletId = null;

        if (user.getOrganizationId() == null) {
            walletId = user.getWalletId();
        } else {
            Organization organization = organizationMapper.findById(user.getOrganizationId());
            walletId = organization.getWalletId();
        }

        if (walletId != null) {
            AccountDto account = accountClient.getAccount(walletId);
            if (account != null) dto.setAccount(account);
        }

        return dto;
    }

    @Override
    public SimpleUserDto userToSimpleDto(User user) {
        return keycloakUserMapper.userToSimpleDto(user);
    }

    @Override
    public List<KeycloakUserDto> listUserToDto(List<User> users) {
        return keycloakUserMapper.listUserToDto(users);
    }

    @Override
    public User userDtoToUser(KeycloakUserDto dto) {
        return keycloakUserMapper.userDtoToUser(dto);
    }

    @Override
    public List<User> listUserDtoToUser(List<KeycloakUserDto> dtos) {
        return keycloakUserMapper.listUserDtoToUser(dtos);
    }
}
