package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.Beneficiary;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class BeneficiaryMapstructMapperDecorator implements BeneficiaryMapstructMapper {
    private UserMapper userMapper;
    private BeneficiaryMapstructMapper mapstructMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

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
        String userName = K.getPreferredUserName();
        User user = userMapper.findByName(userName);

        if (user == null) {
            throw new RuntimeException(String.format("Unknown User (%s)", userName));
        }

        Beneficiary beneficiary = mapstructMapper.dtoToBeneficiary(dto);
        beneficiary.setUserId(user.getId());
        beneficiary.setUserName(user.getUsername());

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

        String userName = K.getPreferredUserName();
        User user = userMapper.findByName(userName);

        if (user == null) {
            throw new RuntimeException(String.format("Unknown User (%s)", userName));
        }

        List<Beneficiary> beneficiaries = mapstructMapper.listDtoToBeneficiary(dtos);

        beneficiaries.forEach(beneficiary -> {
            beneficiary.setUserId(user.getId());
            beneficiary.setUserName(user.getUsername());
        });

        return beneficiaries;
    }
}
