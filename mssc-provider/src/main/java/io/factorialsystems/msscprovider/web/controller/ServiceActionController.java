package io.factorialsystems.msscprovider.web.controller;

import io.factorialsystems.msscprovider.service.ServiceActionService;
import io.factorialsystems.msscprovider.utils.K;
import io.factorialsystems.msscprovider.web.model.MessageDto;
import io.factorialsystems.msscprovider.web.model.ServiceActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/provider/service")
public class ServiceActionController {

    private final ServiceActionService actionService;

    @GetMapping
    public ResponseEntity<?> getServices(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                         @RequestParam(value = "code") String code) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(actionService.getProviderActions(code, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(actionService.getProviderAction(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveService(@Valid @RequestBody ServiceActionDto dto) {
        String userName = K.getUserName();

        try {
            Integer serviceId = actionService.saveAction(userName, dto);
            return new ResponseEntity<>(actionService.getProviderAction(serviceId), HttpStatus.CREATED);
        } catch (Exception ex) {
            String message = String.format("Error Creating Provider Service, System Message : %s", ex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateService(@PathVariable("id") Integer id, @Valid @RequestBody ServiceActionDto dto) {

        try {
            actionService.updateAction(id, dto);
            return new ResponseEntity<>(new MessageDto("Success"), HttpStatus.OK);
        } catch (Exception ex) {
            String message = String.format("Error updating Service, System Message : %s", ex.getMessage());
            return new ResponseEntity<>(new MessageDto(message), HttpStatus.BAD_REQUEST);
        }
    }
}
