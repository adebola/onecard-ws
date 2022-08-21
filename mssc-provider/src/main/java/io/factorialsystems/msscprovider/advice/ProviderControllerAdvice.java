package io.factorialsystems.msscprovider.advice;

import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.exception.FileFormatException;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ws.client.WebServiceIOException;

import java.net.SocketTimeoutException;

@Slf4j
@ControllerAdvice
public class ProviderControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SocketTimeoutException.class, WebServiceIOException.class})
    public ResponseEntity<MessageDto> handleSocketException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto("Unable to reach downstream servers, connection timed out please try again later"), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBindingException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            log.error(String.format("Error in Field %s, message: %s", error.getField(), error.getDefaultMessage()));
        });

        return new ResponseEntity<>(new MessageDto("Invalid JSON Arguments in the REST Submission please contact Onecard Support"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileFormatException.class)
    public ResponseEntity<MessageDto> handleFileFormatException(FileFormatException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
