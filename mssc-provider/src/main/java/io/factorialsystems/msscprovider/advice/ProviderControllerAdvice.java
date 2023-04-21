package io.factorialsystems.msscprovider.advice;

import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.exception.FeignCustomException;
import io.factorialsystems.msscprovider.exception.FileFormatException;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.ws.client.WebServiceIOException;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> handleMethodArgumentNotValid(MethodArgumentNotValidException mex) {
        List<FieldError> error = mex.getBindingResult().getFieldErrors();

        final String errorMessage = error.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

        log.error(errorMessage);
        return new ResponseEntity<>(new MessageDto(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public MessageDto handleGenericException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new MessageDto(exception.getMessage());
    }

    @ExceptionHandler(FeignCustomException.class)
    public ResponseEntity<MessageDto> handleFeignCustomException(FeignCustomException fce) {
        return new ResponseEntity<>(new MessageDto(fce.getMessage()), HttpStatus.valueOf(fce.getStatus()));
    }
}
