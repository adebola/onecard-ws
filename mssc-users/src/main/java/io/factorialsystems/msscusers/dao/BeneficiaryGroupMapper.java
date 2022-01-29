package io.factorialsystems.msscusers.dao;

import io.factorialsystems.msscusers.domain.Beneficiary;
import io.factorialsystems.msscusers.domain.BeneficiaryGroup;
import io.factorialsystems.msscusers.dto.BeneficiaryRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BeneficiaryGroupMapper {
    List<BeneficiaryGroup> findByUserId(String id);
    List<Beneficiary> findBeneficiaries(Integer id);
    BeneficiaryGroup findById(Integer id);
    void removeBeneficiary(BeneficiaryRequestDto dto);
    void addBeneficiary(BeneficiaryRequestDto dto);
    void save(BeneficiaryGroup beneficiaryGroup);
    void update(BeneficiaryGroup beneficiaryGroup);
    void delete(Integer id);
    Integer length(Integer id);
}
