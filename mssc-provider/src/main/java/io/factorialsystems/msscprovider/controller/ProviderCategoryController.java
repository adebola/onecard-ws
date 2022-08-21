package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.service.ProviderCategoryService;
import io.factorialsystems.msscprovider.utils.K;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.dto.provider.ProviderCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/provider/category")
public class ProviderCategoryController {
    private final ProviderCategoryService providerCategoryService;

    @GetMapping
    public ResponseEntity<?> getProviderCategoryList(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(providerCategoryService.findProviderCategories(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProviderCategoryList(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                        @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(providerCategoryService.searchProviderCategories(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderCategoryDto> getProviderCategoryById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(providerCategoryService.findProviderCategoryById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProviderCategory(@Valid @RequestBody ProviderCategoryDto dto) {

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> claims = jwt.getClaims();
        String userName = (String) claims.get("name");

        try {
            Integer providerId = providerCategoryService.saveProviderCategory(userName, dto);
            return new ResponseEntity<>(providerCategoryService.findProviderCategoryById(providerId), HttpStatus.CREATED);
        } catch (DuplicateKeyException dex) {
            String message = String.format("Duplicate Key creating Provider category Name must be unique, System Message : %s", dex.getMessage());
            return new ResponseEntity<>(new MessageDto(message), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProviderCategory(@PathVariable("id") Integer id, @Valid @RequestBody ProviderCategoryDto dto) {

        try {
            providerCategoryService.updateProviderCategory(id, dto);
            return new ResponseEntity<>(new MessageDto("Success"), HttpStatus.OK);
        } catch (DuplicateKeyException dex) {
            String message = String.format("Duplicate Key updating Provider category Name must be unique, System Message : %s", dex.getMessage());
            return new ResponseEntity<>(new MessageDto(message), HttpStatus.BAD_REQUEST);
        }
    }
}
