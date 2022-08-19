package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.SpectranetRingoDataPlan;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
@DecoratedWith(DataPlanMapstructMapperDecorator.class)
public interface DataPlanMapstructMapper {

    @Mappings({
            @Mapping(source = "id", target = "product_id"),
    })
    DataPlanDto ringoPlanToDto(RingoDataPlan plan);
    List<DataPlanDto> listRingoPlanToDto(List<RingoDataPlan> plans);
    DataPlanDto spectranetPlanToDto(SpectranetRingoDataPlan.IndividualPlan plan);
    List<DataPlanDto> listSpectranetPlanToDto(List<SpectranetRingoDataPlan.IndividualPlan> plans);
}
