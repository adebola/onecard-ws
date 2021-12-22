package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface DataPlanMapstructMapper {

    @Mappings({
            @Mapping(source = "id", target = "product_id"),
    })
    DataPlanDto ringoPlanToDto(RingoDataPlan plan);
}
