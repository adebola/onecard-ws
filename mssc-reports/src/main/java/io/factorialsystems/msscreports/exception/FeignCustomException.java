package io.factorialsystems.msscreports.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeignCustomException extends RuntimeException {
    private int status;

    public FeignCustomException(String message, int status) {
        super(String.format("cause: %s response: %d", message, status));
        this.status = status;
    }
}
