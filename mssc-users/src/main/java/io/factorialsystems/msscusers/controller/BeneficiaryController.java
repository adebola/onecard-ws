package io.factorialsystems.msscusers.controller;

import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.dto.BeneficiaryGroupDto;
import io.factorialsystems.msscusers.service.BeneficiaryGroupService;
import io.factorialsystems.msscusers.service.BeneficiaryService;
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
@RequestMapping("/api/v1/beneficiary")
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;
    private final BeneficiaryGroupService beneficiaryGroupService;

    @GetMapping
    public ResponseEntity<List<BeneficiaryDto>> getBeneficiaries() {
        return new ResponseEntity<>(beneficiaryService.getBeneficiaries(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDto> getBeneficiary(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(beneficiaryService.getBeneficiary(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BeneficiaryDto> saveBeneficiary(@Valid @RequestBody BeneficiaryDto dto){
        return new ResponseEntity<>(beneficiaryService.addBeneficiary(dto), HttpStatus.CREATED);
    }

    @PostMapping("/list")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBeneficiaryList(@Valid @RequestBody  List<BeneficiaryDto> beneficiaries) {
        beneficiaryService.addBeneficiaries(beneficiaries);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBeneficiary(@PathVariable("id") Integer id,
                                  @Valid @RequestBody BeneficiaryDto dto) {
        beneficiaryService.updateBeneficiary(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeneficiary(@PathVariable("id") Integer id) {
        beneficiaryService.removeBeneficiary(id);
    }

    @GetMapping("/length/{id}")
    public ResponseEntity<Integer> getGroupLength(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(beneficiaryGroupService.findGroupLength(id), HttpStatus.OK);
    }

    @GetMapping("/group/{id}")
    public ResponseEntity<BeneficiaryGroupDto> getGroup(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(beneficiaryGroupService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/beneficiary/{id}")
    public ResponseEntity<List<BeneficiaryDto>> getGroupBeneficiaries(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(beneficiaryGroupService.findBeneficiaries(id), HttpStatus.OK);
    }
}
