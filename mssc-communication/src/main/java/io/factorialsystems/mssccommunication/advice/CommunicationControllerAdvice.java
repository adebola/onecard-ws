package io.factorialsystems.mssccommunication.advice;

import io.factorialsystems.mssccommunication.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@Slf4j
@ControllerAdvice
public class CommunicationControllerAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageDto> handleAccessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto("UnAuthorized " + exception.getMessage()), HttpStatus.FORBIDDEN);
    }
}
