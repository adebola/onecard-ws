package io.factorialsystems.msscwallet.advice;

import io.factorialsystems.msscwallet.dto.MessageDto;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class WalletControllerAdvise {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.NOT_FOUND);
    }
}
