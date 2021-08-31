package io.factorialsystems.msscprovider.web.mapper.category;

import io.factorialsystems.msscprovider.web.mapper.DateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import io.factorialsystems.msscprovider.domain.ProviderCategory;
import io.factorialsystems.msscprovider.web.model.ProviderCategoryDto;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface ProviderCategoryMapstructMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "categoryName", target = "categoryName"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
    })
    ProviderCategory fromProviderCategory(ProviderCategoryDto dto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "categoryName", target = "categoryName"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate"),
    })
    ProviderCategoryDto toProviderCategoryDto(ProviderCategory providerCategory);

    List<ProviderCategory> fromListProviderCategory(List<ProviderCategoryDto> dto);
    List<ProviderCategoryDto> toListProviderCategoryDto(List<ProviderCategory> providerCategory);
}
