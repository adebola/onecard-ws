package io.factorialsystems.msscwallet.controller;


import io.factorialsystems.msscwallet.dto.SMSVerificationRequestDto;
import io.factorialsystems.msscwallet.dto.kyc.KycSettingDto;
import io.factorialsystems.msscwallet.service.AccountSettingService;
import io.factorialsystems.msscwallet.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kyc")
public class KycController {
    private final KycService kycService;
    private final AccountSettingService accountSettingService;

    @GetMapping("/sms/{msisdn}")
    public ResponseEntity<?> initializeSMSVerification(@PathVariable("msisdn") String msisdn) {
        return ResponseEntity.ok(kycService.startSMSVerification(msisdn));
    }

    @PostMapping("/sms")
    public ResponseEntity<?> finalizeSMSVerification(@Valid @RequestBody SMSVerificationRequestDto dto) {
        return ResponseEntity.ok(kycService.finalizeSMSVerification(dto));
    }

    @GetMapping("/bvn/{id}")
    public ResponseEntity<?> bvnVerify(@PathVariable("id") String bvn) {
        return ResponseEntity.ok(kycService.bvnVerification(bvn));
    }

    @GetMapping("/user-status")
    public ResponseEntity<?> getUserVerificationStatus() {
        return ResponseEntity.ok(kycService.getUserStatus());
    }

    @GetMapping("/admin-status/{id}")
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    public ResponseEntity<?> getAdminVerificationStatus(@PathVariable("id") String id) {
        return ResponseEntity.ok(kycService.getAdminStatus(id));
    }

    @PutMapping("/change")
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changeKycSettings(@Valid @RequestBody KycSettingDto dto) {
        accountSettingService.changeKycSettings(dto);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('Onecard_Admin')")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(accountSettingService.getKyCSettings());
    }
}
