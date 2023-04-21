package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import io.factorialsystems.msscprovider.external.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserClient userClient;

    public Optional<SimpleUserDto> getUserById(String id) {
        return Optional.ofNullable(userClient.getUserById(id));
    }

    public Boolean isUserValid(String id) {
        return getUserById(id).isPresent();
    }
}
