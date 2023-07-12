package io.factorialsystems.msscprovider.recharge.onecard;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.recharge.onecard.response.GenericResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.LoginResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.LoginResponseType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardConnect {
    private final MCrypt loginCrypt;
    private final ObjectMapper objectMapper;

    private String userToken;
    private String authToken;
    private MCrypt apiCrypt = null;
    private Date expiry;

    @Value("${onecard.api.user}")
    private String user;

    @Value("${onecard.api.password}")
    private String password;

    @Value("${onecard.api.baseurl}")
    private String baseUrl;

    @Value("${onecard.api.salt}")
    private String iVSecret;

    private static final int MINUS_FIVE_MINUTES = -5;

    public String decrypt(String s ) throws Exception {
        return new String(apiCrypt.decryptByte(s)).trim();
    }

    public String encrypt(String s) throws Exception {
        return Base64.getEncoder().encodeToString(apiCrypt.encryptByte(s));
    }

    public void logout() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = getHeaders();

        Map<Object, Object> payload = new HashMap<>();
        HttpEntity<?> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/logout", request, String.class);

        final String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        GenericResponse genericResponse = objectMapper.readValue(response, GenericResponse.class);
        log.info("Logout Response {}", genericResponse);
    }

    private LoginResponseType login() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String encryptedUser = Base64.getEncoder().encodeToString(loginCrypt.encryptByte(user));
        String encryptedPassword = Base64.getEncoder().encodeToString(loginCrypt.encryptByte(password));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("username", encryptedUser);
        requestBody.add("pass", encryptedPassword);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, formEntity, String.class);

        String decrypt = new String(loginCrypt.decryptByte(response.getBody())).trim();

        LoginResponse loginResponse = objectMapper.readValue(decrypt, LoginResponse.class);

        if (loginResponse.isResponse() && loginResponse.getResponseCode() == 200) {
            userToken = loginResponse.getResponseData().userToken;
            authToken = loginResponse.getResponseData().authToken;

            MCrypt crypt = new MCrypt(userToken, iVSecret);

            String tempToken = new String(crypt.decryptByte(authToken)).trim();
            String[] split = tempToken.split("~");

            if (split.length != 2) {
                throw new RuntimeException("Split Failed");
            }

            apiCrypt = new MCrypt(userToken, split[1]);
            expiry = new Date(loginResponse.getResponseData().expireAt * 1000);

            log.info("Successful login, token expires at {}", expiry);

            return LoginResponseType.LOGIN_SUCCESS;
        }

        return LoginResponseType.LOGIN_FAILED;
    }

    public synchronized LoginResponseType doLogin() throws Exception {
        if (apiCrypt == null) {
            return login();
        } else  {
            Calendar cal = Calendar.getInstance();
            cal.setTime(expiry);
            cal.add(Calendar.MINUTE, MINUS_FIVE_MINUTES);

            if (cal.after(new Date())) {
                log.info("Logging In Again Expiry {}", expiry);
                logout();
                return login();
            }
        }

        log.info("Already LoggedIn");
        return LoginResponseType.LOGIN_ALREADY_LOGGED_IN;
    }

    public MultiValueMap<String, String> getHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();

        map.put("Content-Type", "application/json");
        map.put("token", userToken);
        map.put("authtoken", authToken);

        headers.setAll(map);

        return headers;
    }
}
