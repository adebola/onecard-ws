package io.factorialsystems.msscprovider.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class RestTemplateInterceptorWithToken implements ClientHttpRequestInterceptor {
    private final String token;

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        HttpHeaders headers = request.getHeaders();
        List<String> authorization = List.of("Bearer " + token);
        headers.addAll("Authorization", authorization);
        return execution.execute(request, body);
    }
}
