package io.factorialsystems.msscusers.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s Not found %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
