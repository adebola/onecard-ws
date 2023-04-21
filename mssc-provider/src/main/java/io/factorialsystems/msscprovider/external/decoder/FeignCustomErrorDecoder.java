package io.factorialsystems.msscprovider.external.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.factorialsystems.msscprovider.exception.FeignCustomException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignCustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        log.error("::Error Invoking {}", response.request().url());
        log.error("::Error Headers {}", response.request().headers());

        String errorMessage = null;

        if (response.body() != null) {
            errorMessage = response.body().toString();
        } else {
            errorMessage = "Error Invoking URL";
        }

        if (response.reason() != null) {
            log.error("::Error Reason {}", response.reason());
        }

        log.error("::Error Message {}", errorMessage);
        return new FeignCustomException(errorMessage, response.status());
    }
}
