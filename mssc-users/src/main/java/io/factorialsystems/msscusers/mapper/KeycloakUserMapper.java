package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateTimestampMapper.class})
@DecoratedWith(KeycloakUserMapperDecorator.class)
public interface KeycloakUserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdTimestamp", target = "createdDate"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "enabled", target = "enabled"),
            @Mapping(source = "emailVerified", target = "emailVerified"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "requiredActions", target = "requiredActions")
    })
    KeycloakUserDto userRepresentationToDto(UserRepresentation userRepresentation);
    List<KeycloakUserDto> listUserRepresentationToDto(List<UserRepresentation> userRepresentations);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "enabled", target = "enabled"),
            @Mapping(source = "emailVerified", target = "emailVerified"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "id", target = "token"),
            @Mapping(source = "organizationId", target = "organizationId"),
            @Mapping(source = "profilePicture", target = "profilePicture")

    })
    KeycloakUserDto userToDto(User user);
    List<KeycloakUserDto> listUserToDto(List<User> users);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "enabled", target = "enabled"),
            @Mapping(source = "emailVerified", target = "emailVerified"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "organizationId", target = "organizationId"),
            @Mapping(source = "profilePicture", target = "profilePicture")
    })
    User userDtoToUser(KeycloakUserDto dto);
    List<User> listUserDtoToUser(List<KeycloakUserDto> dtos);
}
