package io.factorialsystems.msscprovider.advice;

import io.factorialsystems.msscprovider.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MessageDto> handleSocketException(SocketTimeoutException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto("Unable to reach downstream servers, connection timed out please try again later"), HttpStatus.BAD_GATEWAY);
    }
}
