package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.AutoRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.AutoUploadFileRechargeRequestDto;
import io.factorialsystems.msscprovider.service.AutoRechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth-recharge/auto")
public class AutoRechargeAuthController {
    private final AutoRechargeService autoRechargeService;

    @PostMapping
    public ResponseEntity<?> startAutoRecharge(@Valid @RequestBody AutoRechargeRequestDto dto) {
        return new ResponseEntity<>(autoRechargeService.saveService(dto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAutoRecharge(@PathVariable("id") String id) {
        return new ResponseEntity<>(autoRechargeService.getSingleService(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAutoRecharge(@PathVariable("id") String id, @Valid @RequestBody AutoRechargeRequestDto dto) {
        autoRechargeService.updateService(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAutoRecharge(@PathVariable("id") String id) {
        autoRechargeService.deleteService(id);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserAutoRecharges() {
        return new ResponseEntity<>(autoRechargeService.findUserRecharges(), HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadAutoFile(@RequestPart(value = "auto") AutoUploadFileRechargeRequestDto dto, @RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<>(autoRechargeService.uploadRecharge(dto, file), HttpStatus.ACCEPTED);
    }
}
