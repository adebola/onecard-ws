package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.BeneficiaryGroup;
import io.factorialsystems.msscusers.dto.BeneficiaryGroupDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
@DecoratedWith(BeneficiaryGroupMapstructMapperDecorator.class)
public interface BeneficiaryGroupMapstructMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "groupName", target = "groupName"),
    })
    BeneficiaryGroup dtoToBeneficiary(BeneficiaryGroupDto dto);

    @Mappings({
            @Mapping(source= "id", target = "id"),
            @Mapping(source = "groupName", target = "groupName"),
    })
    BeneficiaryGroupDto beneficiaryToDto(BeneficiaryGroup beneficiary);
    List<BeneficiaryGroupDto> listBeneficiaryToDto (List<BeneficiaryGroup> beneficiaries);
    List<BeneficiaryGroup> listDtoToBeneficiary(List<BeneficiaryGroupDto> dtos);
}
