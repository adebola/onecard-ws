package io.factorialsystems.msscprovider.web.controller;

import io.factorialsystems.msscprovider.service.ProviderService;
import io.factorialsystems.msscprovider.utils.K;
import io.factorialsystems.msscprovider.web.model.MessageDto;
import io.factorialsystems.msscprovider.web.model.ProviderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/provider")
public class ProviderController {

    private final Environment environment;
    private final ProviderService providerService;

    @GetMapping("/status")
    public String status() {
        final String status = K.SERVICE_STATUS + environment.getProperty("local.server.port");

        log.info(status);
        return status;
    }

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

    @PostMapping
    public ResponseEntity<?> createProvider(@Valid @RequestBody ProviderDto dto) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        String userName = (String) claims.get("name");

        try {
            Integer providerId = providerService.saveProvider(userName, dto);
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
}
