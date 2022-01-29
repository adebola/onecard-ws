package io.factorialsystems.msscusers.advice;

import io.factorialsystems.msscusers.dto.MessageDto;
import io.factorialsystems.msscusers.exceptions.NoPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDto> handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<MessageDto> handleDuplicate(DuplicateKeyException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto("Duplicate Exception The Beneficiary / Beneficiaries or Group added may already exist please check especially the phone Number(s)"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<MessageDto> handleNoPermission(NoPermissionException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(new MessageDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
