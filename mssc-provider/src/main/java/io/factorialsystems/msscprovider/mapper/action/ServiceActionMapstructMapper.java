package io.factorialsystems.msscprovider.mapper.action;


import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.ServiceActionDto;
import org.mapstruct.Mapper;
import org.mapstruct.DecoratedWith;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(ServiceActionMapstructMapperDecorator.class)
public interface ServiceActionMapstructMapper {

    @Mappings({
            @Mapping(target = "providerCode", ignore = true),
            @Mapping(target = "providerName", ignore = true),
            @Mapping(target = "actionName", ignore = true)
    })
    ServiceAction actionDtoToAction(ServiceActionDto dto);
    ServiceActionDto actionToActionDto(ServiceAction action);
    List<ServiceAction> listActionDtoToAction (List<ServiceActionDto> dtoList);
    List<ServiceActionDto> listActionToActionDto(List<ServiceAction> actions);
}
