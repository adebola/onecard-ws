package io.factorialsystems.msscapiuser.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscapiuser.dto.request.RealmRequestDto;
import io.factorialsystems.msscapiuser.dto.request.UserRequestDto;
import io.factorialsystems.msscapiuser.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class Keycloak {

    @Value("${keycloak.serverurl}")
    private String authUrl;

    @Value("${keycloak.client-id}")
    private String client_id;

    @Value("${keycloak.realm-user}")
    private String realmUser;

    @Value("${keycloak.realm-password}")
    private String realmPassword;

    private final ObjectMapper objectMapper;

    public String getUserToken(String userId) {
        RealmRequestDto realmRequestDto = RealmRequestDto.builder()
                .client_id(client_id)
                .grant_type("password")
                .password(realmPassword)
                .username(realmUser)
                .scope("openid")
                .build();

        RestTemplate restTemplate = new RestTemplate();

        // Get the Realm Administrator Token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = null;

        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(realmRequestDto), headers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ResponseEntity<TokenResponseDto> response =
                restTemplate.exchange (authUrl, HttpMethod.POST, request, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .client_id(client_id)
                .grant_type("urn:ietf:params:oauth:grant-type:token-exchange")
                .scope("openid")
                .subject_token(token.getAccess_token())
                .build();

        // Get User's Token
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> userRequest = null;

        try {
            userRequest = new HttpEntity<>(objectMapper.writeValueAsString(userRequestDto), userHeaders);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ResponseEntity<TokenResponseDto> userResponse =
                restTemplate.exchange (authUrl, HttpMethod.POST, userRequest, TokenResponseDto.class);

        return Objects.requireNonNull(userResponse.getBody()).getAccess_token();
    }
}
