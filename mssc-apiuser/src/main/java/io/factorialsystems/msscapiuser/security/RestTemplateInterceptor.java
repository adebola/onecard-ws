package io.factorialsystems.msscapiuser.security;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest,
                                        byte[] body,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        final String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        HttpHeaders headers = httpRequest.getHeaders();
        List<String> authorization = List.of("Bearer " + token);
        headers.addAll("Authorization", authorization);
        return clientHttpRequestExecution.execute(httpRequest, body);
    }
}
