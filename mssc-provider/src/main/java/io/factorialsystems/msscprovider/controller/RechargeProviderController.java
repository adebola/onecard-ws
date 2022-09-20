package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.provider.RechargeProviderDto;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderExDto;
import io.factorialsystems.msscprovider.service.RechargeProviderService;
import io.factorialsystems.msscprovider.utils.Constants;
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
@RequestMapping("/api/v1/provider/recharge")
public class RechargeProviderController {
    private final RechargeProviderService rechargeProviderService;

    @GetMapping
    public ResponseEntity<?> getAllRechargeProviders(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(rechargeProviderService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RechargeProviderDto> getRechargeProvider(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(rechargeProviderService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/service/{id}")
    public ResponseEntity<List<RechargeProviderExDto>> getRechargeProviderByService(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(rechargeProviderService.findByServiceId(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RechargeProviderDto> saveRechargeProvider(@Valid @RequestBody RechargeProviderDto dto) {
        return new ResponseEntity<>(rechargeProviderService.save(dto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RechargeProviderDto> updateRechargeProvider(@PathVariable("id") Integer id,
                                                                      @Valid @RequestBody RechargeProviderDto dto) {
        return new ResponseEntity<>(rechargeProviderService.update(id, dto), HttpStatus.OK);
    }
}
