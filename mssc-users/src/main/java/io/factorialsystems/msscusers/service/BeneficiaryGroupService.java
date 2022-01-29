package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dao.BeneficiaryGroupMapper;
import io.factorialsystems.msscusers.domain.BeneficiaryGroup;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.dto.BeneficiaryGroupDto;
import io.factorialsystems.msscusers.dto.BeneficiaryRequestDto;
import io.factorialsystems.msscusers.mapper.BeneficiaryGroupMapstructMapper;
import io.factorialsystems.msscusers.mapper.BeneficiaryMapstructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeneficiaryGroupService {
    private final BeneficiaryGroupMapper beneficiaryGroupMapper;
    private final BeneficiaryMapstructMapper beneficiaryMapstructMapper;
    private final BeneficiaryGroupMapstructMapper beneficiaryGroupMapstructMapper;

    public List<BeneficiaryGroupDto> findByUserId(String id) {
        return beneficiaryGroupMapstructMapper.listBeneficiaryToDto(beneficiaryGroupMapper.findByUserId(id));
    }

    public List<BeneficiaryDto> findBeneficiaries(Integer id) {
        return beneficiaryMapstructMapper.listBeneficiaryToDto(beneficiaryGroupMapper.findBeneficiaries(id));
    }

    public BeneficiaryGroupDto findById(Integer id) {
        return beneficiaryGroupMapstructMapper.beneficiaryToDto(beneficiaryGroupMapper.findById(id));
    }

    public void removeBeneficiary(BeneficiaryRequestDto dto) {
        beneficiaryGroupMapper.removeBeneficiary(dto);
    }

    public void addBeneficiary(BeneficiaryRequestDto dto) {
        beneficiaryGroupMapper.addBeneficiary(dto);
    }

    public BeneficiaryGroupDto save (BeneficiaryGroup beneficiaryGroup) {
        beneficiaryGroupMapper.save(beneficiaryGroup);
        return beneficiaryGroupMapstructMapper.beneficiaryToDto(beneficiaryGroup);
    }

    public void update(Integer id, BeneficiaryGroup beneficiaryGroup) {
        beneficiaryGroup.setId(id);
        beneficiaryGroupMapper.save(beneficiaryGroup);
    }

    public void delete(Integer id) {
        beneficiaryGroupMapper.delete(id);
    }

    public Integer findGroupLength(Integer id) {
        return beneficiaryGroupMapper.length(id);
    }
}
