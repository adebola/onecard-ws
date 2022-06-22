package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.dao.OrganizationMapper;
import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.SimpleUserDto;
import io.factorialsystems.msscusers.security.RestTemplateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
public class KeycloakUserMapperDecorator implements KeycloakUserMapper {
    private KeycloakUserMapper keycloakUserMapper;
    private OrganizationMapper organizationMapper;

    @Value("${api.host.baseurl}")
    private String apiHost;

    public static final String ACCOUNT_PATH = "/api/v1/account";

    @Autowired
    public void setKeycloakUserMapper(KeycloakUserMapper keycloakUserMapper) {
        this.keycloakUserMapper = keycloakUserMapper;
    }

    @Autowired
    public void setOrganizationMapper(OrganizationMapper organizationMapper) {
       this.organizationMapper = organizationMapper;
    }

    @Override
    public KeycloakUserDto userRepresentationToDto(UserRepresentation userRepresentation) {
        return keycloakUserMapper.userRepresentationToDto(userRepresentation);
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
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());

            Optional<AccountDto> accountDto =
                    Optional.ofNullable(restTemplate.getForObject(apiHost + ACCOUNT_PATH + "/" + walletId, AccountDto.class));

            accountDto.ifPresent(dto::setAccount);
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
