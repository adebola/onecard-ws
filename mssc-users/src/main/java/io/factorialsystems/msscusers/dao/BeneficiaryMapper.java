package io.factorialsystems.msscusers.dao;

import io.factorialsystems.msscusers.domain.Beneficiary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BeneficiaryMapper {
    List<Beneficiary> findByUserId(String id);
    List<Beneficiary> findByUserName(String name);
    Beneficiary findById(Integer id);
    void save(Beneficiary beneficiary);
    void update(Beneficiary beneficiary);
    void saveList(List<Beneficiary> beneficiaries);
    void delete(Integer id);
}
