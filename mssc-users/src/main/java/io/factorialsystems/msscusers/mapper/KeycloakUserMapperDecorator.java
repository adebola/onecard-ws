package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.security.RestTemplateInterceptor;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
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

    @Value("${api.host.baseurl}")
    private String apiHost;

    public static final String ACCOUNT_PATH = "/api/v1/account";

    @Autowired
    public void setKeycloakUserMapper(KeycloakUserMapper keycloakUserMapper) {
        this.keycloakUserMapper = keycloakUserMapper;
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

        if (user.getWalletId() != null) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());

            Optional<AccountDto> accountDto =
                    Optional.ofNullable(restTemplate.getForObject(apiHost + ACCOUNT_PATH + "/" + user.getWalletId(), AccountDto.class));

            accountDto.ifPresent(dto::setAccount);
        }

        return dto;
    }

    @Override
    public List<KeycloakUserDto> listUserToDto(List<User> users) {
        return keycloakUserMapper.listUserToDto(users);
    }
}
