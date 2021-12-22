package io.factorialsystems.msscusers.exceptions;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException(String errorMessage) {
        super(errorMessage);
    }
}
