package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.dto.provider.ProviderDto;
import io.factorialsystems.msscprovider.service.ProviderService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/provider")
public class ProviderController {

    private final Environment environment;
    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<?> getProviderList(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                             @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(providerService.findProviders(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProviderList(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(providerService.searchProviders(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProviderDto> getProviderById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(providerService.findProviderById(id), HttpStatus.OK);
    }

    @GetMapping("/bycategory/{name}")
    ResponseEntity<List<ProviderDto>> getProviderByCategory(@PathVariable("name") String name) {
        return new ResponseEntity<>(providerService.findByCategory(name), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProvider(@Valid @RequestBody ProviderDto dto) {

        try {
            Integer providerId = providerService.saveProvider(K.getUserName(), dto);
            return new ResponseEntity<>(providerService.findProviderById(providerId), HttpStatus.CREATED);
        } catch (DuplicateKeyException dex) {
            String message = String.format("Duplicate Key creating provider Code must be unique, System Message : %s", dex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateProvider(@PathVariable("id") Integer id, @Valid @RequestBody ProviderDto dto) {

        try {
            providerService.updateProvider(id, dto);
            return new ResponseEntity<>(new MessageDto("Success"), HttpStatus.OK);
        } catch (DuplicateKeyException dex) {
            String message = String.format("Duplicate Key updating provider Code must be unique, System Message : %s", dex.getMessage());
            return new ResponseEntity<>(new MessageDto(message), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/activate/{id}")
    public ResponseEntity<?> activateProvider(@PathVariable("id") Integer id) {

        ProviderDto dto = providerService.activateProvider(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("Invalid Id %d activation failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/suspend/{id}")
    public ResponseEntity<?> suspendProvider(@PathVariable("id") Integer id) {
        ProviderDto dto = providerService.suspendProvider(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("Invalid Id %d suspension failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @GetMapping("/unsuspend/{id}")
    public ResponseEntity<?> unsuspendProvider(@PathVariable("id") Integer id) {
        ProviderDto dto = providerService.unsuspendProvider(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto(String.format("Invalid Id %d un-suspension failed", id)), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
