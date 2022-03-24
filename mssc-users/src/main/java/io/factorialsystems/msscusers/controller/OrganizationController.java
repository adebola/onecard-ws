package io.factorialsystems.msscusers.controller;

import io.factorialsystems.msscusers.dto.OrganizationDto;
import io.factorialsystems.msscusers.dto.PagedDto;
import io.factorialsystems.msscusers.service.OrganizationService;
import io.factorialsystems.msscusers.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/organization")
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<PagedDto<OrganizationDto>> getOrganizations(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(organizationService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<OrganizationDto> getOrganizationById(@PathVariable("id") String id) {
        return new ResponseEntity<>(organizationService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<PagedDto<OrganizationDto>> searchOrganizations(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                         @RequestParam(value = "searchString") String searchString) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(organizationService.search(searchString, pageNumber, pageSize), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable("id") String id, @Valid @RequestBody OrganizationDto dto) {
        return new ResponseEntity<>(organizationService.update(id, dto), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<OrganizationDto> saveOrganization(@Valid @RequestBody OrganizationDto dto) {
        return new ResponseEntity<>(organizationService.save(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void deleteOrganization(@PathVariable("id") String id) {
        organizationService.delete(id);
    }
}
