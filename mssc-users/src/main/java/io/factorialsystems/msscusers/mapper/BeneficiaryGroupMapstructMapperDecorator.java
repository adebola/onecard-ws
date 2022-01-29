package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.dao.BeneficiaryGroupMapper;
import io.factorialsystems.msscusers.domain.BeneficiaryGroup;
import io.factorialsystems.msscusers.dto.BeneficiaryGroupDto;
import io.factorialsystems.msscusers.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BeneficiaryGroupMapstructMapperDecorator implements BeneficiaryGroupMapstructMapper {
    private BeneficiaryGroupMapper mapper;
    private BeneficiaryGroupMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(BeneficiaryGroupMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setMapper(BeneficiaryGroupMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BeneficiaryGroup dtoToBeneficiary(BeneficiaryGroupDto dto) {
        BeneficiaryGroup beneficiaryGroup = mapstructMapper.dtoToBeneficiary(dto);
        return convertGroup(dto, beneficiaryGroup);
    }

    @Override
    public BeneficiaryGroupDto beneficiaryToDto(BeneficiaryGroup beneficiary) {
        return mapstructMapper.beneficiaryToDto(beneficiary);
    }

    @Override
    public List<BeneficiaryGroupDto> listBeneficiaryToDto(List<BeneficiaryGroup> beneficiaries) {
        return mapstructMapper.listBeneficiaryToDto(beneficiaries);
    }

    @Override
    public List<BeneficiaryGroup> listDtoToBeneficiary(List<BeneficiaryGroupDto> dtos) {

        List<BeneficiaryGroup> beneficiaryGroups = new ArrayList<>(dtos.size());

        dtos.forEach( dto -> {
            BeneficiaryGroup beneficiaryGroup = new BeneficiaryGroup();
            beneficiaryGroup.setGroupName(dto.getGroupName());
            beneficiaryGroups.add(convertGroup(dto, beneficiaryGroup));
        });

        return beneficiaryGroups;
    }

    private BeneficiaryGroup convertGroup(BeneficiaryGroupDto dto, BeneficiaryGroup beneficiaryGroup){

        if (dto.getId() != null) {
            BeneficiaryGroup group = mapper.findById(dto.getId());

            if (group == null ) {
                final String message = String.format("Invalid Group with Id (%d)", dto.getId());

                log.error(message);
                throw new RuntimeException(message);
            }

            if (!group.getUserId().equals(K.getUserId())) {
                final String message =
                        String.format("Permission Error, you do not have permissions to access group (%d/%s)", group.getId(), group.getGroupName());

                log.error(message);
                throw new RuntimeException(message);
            }

            beneficiaryGroup.setUserId(group.getUserId());
            beneficiaryGroup.setUserName(group.getUserName());
        }

        return beneficiaryGroup;
    }
}
