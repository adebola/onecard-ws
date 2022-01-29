package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.Beneficiary;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class BeneficiaryMapstructMapperDecorator implements BeneficiaryMapstructMapper {
    private BeneficiaryMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(BeneficiaryMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Override
    public BeneficiaryDto beneficiaryToDto(Beneficiary beneficiary) {
        return mapstructMapper.beneficiaryToDto(beneficiary);
    }

    @Override
    public Beneficiary dtoToBeneficiary(BeneficiaryDto dto) {
        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User Not found in Token (dtoToBeneficiary), please LogIn to carry out this operation");
        }

        Beneficiary beneficiary = mapstructMapper.dtoToBeneficiary(dto);
        beneficiary.setUserId(userId);
        beneficiary.setUserName(K.getPreferredUserName());

        return beneficiary;
    }

    @Override
    public List<BeneficiaryDto> listBeneficiaryToDto(List<Beneficiary> beneficiaries) {
        return mapstructMapper.listBeneficiaryToDto(beneficiaries);
    }

    @Override
    public List<Beneficiary> listDtoToBeneficiary(List<BeneficiaryDto> dtos) {

        if ( dtos == null ) {
            return null;
        }

        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User Not found in Token (listDtoToBeneficiary), please LogIn to carry out this operation");
        }

        List<Beneficiary> beneficiaries = mapstructMapper.listDtoToBeneficiary(dtos);
        String userName = K.getPreferredUserName();

        beneficiaries.forEach(beneficiary -> {
            beneficiary.setUserId(userId);
            beneficiary.setUserName(userName);
        });

        return beneficiaries;
    }
}
