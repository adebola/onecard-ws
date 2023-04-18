package io.factorialsystems.msscapiuser.external.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.factorialsystems.msscapiuser.exception.FeignCustomException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignCustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        log.error("::Error Invoking {}", response.request().url());
        log.error("::Error Headers {}", response.request().headers());

        String message = null;

        if (response.body() == null) {
            message = "Error Invoking URL";
        } else {
            message = response.body().toString();
        }

        log.error("::Error Message {}", message);
        return new FeignCustomException(message, response.status(), response.request().url());
    }
}
