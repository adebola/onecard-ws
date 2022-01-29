package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dao.BeneficiaryMapper;
import io.factorialsystems.msscusers.domain.Beneficiary;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.exceptions.NoPermissionException;
import io.factorialsystems.msscusers.mapper.BeneficiaryMapstructMapper;
import io.factorialsystems.msscusers.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeneficiaryService {
    private final BeneficiaryMapper beneficiaryMapper;
    private final BeneficiaryMapstructMapper mapstructMapper;

    public BeneficiaryDto addBeneficiary(BeneficiaryDto beneficiaryDto) {
        Beneficiary beneficiary = mapstructMapper.dtoToBeneficiary(beneficiaryDto);
        beneficiaryMapper.save(beneficiary);
        return mapstructMapper.beneficiaryToDto(beneficiary);
    }

    public void addBeneficiaries(List<BeneficiaryDto> dtos) {
        List<Beneficiary> beneficiaries = mapstructMapper.listDtoToBeneficiary(dtos);
        beneficiaryMapper.saveList(beneficiaries);
    }

    public void removeBeneficiary(Integer id) {
        Beneficiary beneficiary = beneficiaryMapper.findById(id);

        if (beneficiary != null && beneficiary.getUserId().equals(K.getUserId())) {
            beneficiaryMapper.delete(id);
            return;
        }

        final String errorMessage = "You do not have the Permission for this DELETE operation";
        log.error(errorMessage);
        throw new NoPermissionException(errorMessage);
    }

    public void updateBeneficiary(Integer id, BeneficiaryDto beneficiaryDto) {
        beneficiaryDto.setId(id);
        Beneficiary beneficiary = mapstructMapper.dtoToBeneficiary(beneficiaryDto);

        if (beneficiary.getUserId().equals(K.getUserId())) {
            beneficiaryMapper.update(beneficiary);
            return;
        }

        final String errorMessage = "You do not have the Permission for this UPDATE operation";
        log.error(errorMessage);
        throw new NoPermissionException(errorMessage);
    }

    public BeneficiaryDto getBeneficiary(Integer id) {
        Beneficiary beneficiary = beneficiaryMapper.findById(id);

        if (beneficiary.getUserId().equals(K.getUserId())) {
            return mapstructMapper.beneficiaryToDto(beneficiary);
        }

        final String errorMessage = "You do not have the Permission for this GET BENEFICIARY operation";
        log.error(errorMessage);

        throw new NoPermissionException(errorMessage);
    }

    public List<BeneficiaryDto> getBeneficiaries() {
        String userName = K.getPreferredUserName();

        List<Beneficiary> beneficiaries = beneficiaryMapper.findByUserName(userName);

        if (beneficiaries == null) {
            log.error("Unexpected Error in BeneficiaryService::getUserBeneficiariesByName findByName returned NULL Pointer");
            throw new RuntimeException("Unexpected Error Getting Beneficiaries for User from the Database");
        }

        if (beneficiaries.size() > 0 && beneficiaries.get(0) != null) {
            if (beneficiaries.get(0).getUserName().equals(K.getPreferredUserName())) {
                return mapstructMapper.listBeneficiaryToDto(beneficiaries);
            }
        } else {
            return Collections.emptyList();
        }

        final String errorMessage = "You do not have the Permission for this GET BENEFICIARIES BY USERNAME operation";
        log.error(errorMessage);

        throw new NoPermissionException(errorMessage);
    }
}

