package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.Beneficiary;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
@DecoratedWith(BeneficiaryMapstructMapperDecorator.class)
public interface BeneficiaryMapstructMapper {

    @Mappings({
            @Mapping(target = "createdOn", ignore = true),
            @Mapping(target = "userId", ignore = true)
    })
    Beneficiary dtoToBeneficiary(BeneficiaryDto dto);
    BeneficiaryDto beneficiaryToDto(Beneficiary beneficiary);
    List<BeneficiaryDto> listBeneficiaryToDto (List<Beneficiary> beneficiaries);
    List<Beneficiary> listDtoToBeneficiary(List<BeneficiaryDto> dtos);
}
