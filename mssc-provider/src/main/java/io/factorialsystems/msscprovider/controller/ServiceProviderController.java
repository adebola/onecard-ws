package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.service.ProviderService;
import io.factorialsystems.msscprovider.service.ServiceActionService;
import io.factorialsystems.msscprovider.utils.K;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.ProviderDto;
import io.factorialsystems.msscprovider.dto.ServiceActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/serviceprovider")
public class ServiceProviderController {
    private final ProviderService providerService;
    private final ServiceActionService actionService;

    @GetMapping("/{type}")
    ResponseEntity<List<ProviderDto>> getProviderByCategory(@PathVariable("type") String type) {
        return new ResponseEntity<>(providerService.findByCategory(type), HttpStatus.OK);
    }

    @GetMapping("/{code}/plans")
    ResponseEntity<PagedDto<ServiceActionDto>> getProviderServices(@PathVariable("code") String code,
                                                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(actionService.getProviderActions(code, pageNumber, pageSize), HttpStatus.OK);
    }
}
