package io.factorialsystems.msscapiuser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecurityService {
    public HttpHeaders getHttpHeaders() {
        final String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        return headers;
    }
}
