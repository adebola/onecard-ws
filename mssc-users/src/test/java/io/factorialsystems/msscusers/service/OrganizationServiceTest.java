package io.factorialsystems.msscusers.service;

import com.github.pagehelper.Page;
import io.factorialsystems.msscusers.dao.OrganizationMapper;
import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.OrganizationDto;
import io.factorialsystems.msscusers.mapper.KeycloakUserMapper;
import io.factorialsystems.msscusers.mapper.OrganizationMapstructMapper;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@CommonsLog
@SpringBootTest
class OrganizationServiceTest {
    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    OrganizationMapstructMapper mapstructMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    KeycloakUserMapper keycloakUserMapper;

    @Test
    public void save() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String createdBy = "debug_test";
        final String accessToken = getUserToken(id);

        try (MockedStatic<K> security  = Mockito.mockStatic(K.class)) {
            security.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);

            security.when(K::getUserName).thenReturn(createdBy);
            assert Objects.equals(K.getUserName(), createdBy);

            security.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);
            log.info(K.getAccessToken());

            OrganizationDto organizationDto = new OrganizationDto();
            organizationDto.setOrganizationName("Test-Organization");
            OrganizationDto save = organizationService.save(organizationDto);
            log.info(save);
        }
    }

    @Test
    public void organizationToDto() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String createdBy = "debug_test";
        final String accessToken = getUserToken(id);

        try (MockedStatic<K> security  = Mockito.mockStatic(K.class)) {
            security.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);

            security.when(K::getUserName).thenReturn(createdBy);
            assert Objects.equals(K.getUserName(), createdBy);

            security.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);
            log.info(K.getAccessToken());


            Page<Organization> all = organizationMapper.findAll();
            Organization o = all.get(0);
            log.info(o);

            OrganizationDto organizationDto = mapstructMapper.organizationToDto(o);
            log.info(organizationDto);
        }
    }

    @Test
    public void userToDto() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String createdBy = "debug_test";
        final String walletId = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
        final String accessToken = getUserToken(id);

        try (MockedStatic<K> security  = Mockito.mockStatic(K.class)) {
            security.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);

            security.when(K::getUserName).thenReturn(createdBy);
            assert Objects.equals(K.getUserName(), createdBy);

            security.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);
            log.info(K.getAccessToken());

            User u = new User();
            u.setId(id);
            u.setWalletId(walletId);

            KeycloakUserDto keycloakUserDto = keycloakUserMapper.userToDto(u);
            log.info(keycloakUserDto);
        }

    }

    private String getRealmAdminToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "password");
        requestBody.add("password", realmPassword);
        requestBody.add("username", realmUser);
        requestBody.add("scope", "openid");

        // Get the Realm Administrator Token
        return getToken(requestBody);
    }

    private String getUserToken(String userId) {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
    }

    private String getUserToken(String userId, String realmToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", realmToken);
        requestBody.add("requested_subject", userId);

        return getToken(requestBody);
    }

    private String getToken(MultiValueMap<String, String> requestBody) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponseDto> response =
                restTemplate.exchange (authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        return  token.getAccess_token();
    }
}