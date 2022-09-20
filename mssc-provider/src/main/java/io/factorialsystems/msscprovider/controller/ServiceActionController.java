package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.domain.Action;
import io.factorialsystems.msscprovider.domain.ProviderServiceRechargeProvider;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.dto.ServiceActionDto;
import io.factorialsystems.msscprovider.service.ServiceActionService;
import io.factorialsystems.msscprovider.utils.Constants;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(actionService.getProviderActions(code, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllServices(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(actionService.getAllServices(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(actionService.getProviderAction(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServiceActionDto> saveService(@Valid @RequestBody ServiceActionDto dto) {
        return new ResponseEntity<>( actionService.saveAction(ProviderSecurity.getUserName(), dto), HttpStatus.CREATED);
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

    @GetMapping("/activate/{id}")
    public ResponseEntity<?> activateService(@PathVariable("id") Integer id) {
        ServiceActionDto dto = actionService.activateService(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("ServiceAction Invalid Id %d activation failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/suspend/{id}")
    public ResponseEntity<?> suspendService(@PathVariable("id") Integer id) {
        ServiceActionDto dto = actionService.suspendService(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("ServiceAction Invalid Id %d activation failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/unsuspend/{id}")
    public ResponseEntity<?> unsuspendService(@PathVariable("id") Integer id) {
        ServiceActionDto dto = actionService.unsuspendService(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("ServiceAction Invalid Id %d activation failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/actions")
    public ResponseEntity<List<Action>> getActions() {
        return new ResponseEntity<>(actionService.getActions(), HttpStatus.OK);
    }

    @GetMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRechargeProviderFromService(@RequestParam("rechargeId") Integer rechargeId,
                                                  @RequestParam("serviceId") Integer serviceId) {
        actionService.removeRechargeProviderFromService(rechargeId, serviceId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRechargeProviderToService(@Valid @RequestBody ProviderServiceRechargeProvider psrp) {
        return new ResponseEntity<>(actionService.addRechargeProviderToService(psrp), HttpStatus.OK);
    }

    @PutMapping("/amend")
    public ResponseEntity<?> amendRechargeProviderService(@Valid @RequestBody ProviderServiceRechargeProvider psrp) {
       return new ResponseEntity<>(actionService.amendRechargeProviderService(psrp), HttpStatus.OK);
    }
}
