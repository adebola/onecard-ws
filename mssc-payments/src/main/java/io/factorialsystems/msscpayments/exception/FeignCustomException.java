package io.factorialsystems.msscpayments.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeignCustomException extends RuntimeException {
    private int status;
    private String url;

    public FeignCustomException(String message, int status, String url) {
        super(String.format("Error invoking %s message %s status %d", url, message, status));
        this.status = status;
    }
}
