package io.factorialsystems.msscprovider.security;


import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest,
                                        byte[] body,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        HttpHeaders headers = httpRequest.getHeaders();
        List<String> authorization = List.of("Bearer " + ProviderSecurity.getAccessToken());
        headers.addAll("Authorization", authorization);
        return clientHttpRequestExecution.execute(httpRequest, body);
    }
}
