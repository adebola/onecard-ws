package io.factorialsystems.msscprovider.mapper.provider;

import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import io.factorialsystems.msscprovider.dto.provider.ProviderDto;
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
            @Mapping(source = "activatedBy", target = "activatedBy"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "activationDate", target = "activationDate"),
            @Mapping(source = "suspended", target = "suspended"),
            @Mapping(target = "category", ignore = true),
    })
    Provider providerDtoToProvider(ProviderDto providerDto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "code", target = "code"),
            @Mapping(source = "activated", target = "activated"),
            @Mapping(source = "activatedBy", target = "activatedBy"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "activationDate", target = "activationDate"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "suspended", target = "suspended"),
    })
    ProviderDto providerToProviderDto(Provider provider);

    List<Provider> listProviderDtoToProvider(List<ProviderDto> dto);
    List<ProviderDto> listProviderToProviderDto(List<Provider> providers);
}
