package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dto.SimpleUserDto;
import io.factorialsystems.msscwallet.security.RestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserService {
    @Value("${api.host.baseurl}")
    private String baseUrl;

    public Optional<SimpleUserDto> getUserById(String id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        SimpleUserDto simpleDto = restTemplate.getForObject(baseUrl + "/api/v1/user/simple/" + id, SimpleUserDto.class);

        if (simpleDto != null) {
            return Optional.of(simpleDto);
        }

        return Optional.empty();
    }
}
