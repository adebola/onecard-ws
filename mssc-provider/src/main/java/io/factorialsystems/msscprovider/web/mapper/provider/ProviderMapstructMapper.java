package io.factorialsystems.msscprovider.web.mapper.provider;

import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.web.mapper.DateMapper;
import io.factorialsystems.msscprovider.web.model.ProviderDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(ProviderMapstructMapperDecorator.class)
public interface ProviderMapstructMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "code", target = "code"),
            @Mapping(source = "activated", target = "activated"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "activationDate", target = "activationDate"),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "status", ignore = true)
    })
    Provider providerDtoToProvider(ProviderDto providerDto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "code", target = "code"),
            @Mapping(source = "activated", target = "activated"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "activationDate", target = "activationDate"),
            @Mapping(source = "category", target = "category"),
            @Mapping(target = "status", ignore = true)
    })
    ProviderDto providerToProviderDto(Provider provider);

    List<Provider> listProviderDtoToProvider(List<ProviderDto> dto);
    List<ProviderDto> listProviderToProviderDto(List<Provider> providers);
}
