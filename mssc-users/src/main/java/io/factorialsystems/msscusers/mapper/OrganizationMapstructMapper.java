package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.dto.OrganizationDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(OrganizationMapstructMapperDecorator.class)
public interface OrganizationMapstructMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(source = "organizationName", target = "organizationName"),
            @Mapping(target = "walletId", ignore = true),
    })
    Organization dtoToOrganization(OrganizationDto dto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "organizationName", target = "organizationName"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    OrganizationDto organizationToDto(Organization organization);
    List<OrganizationDto> listOrganizationToDto(List<Organization> organizations);
}
