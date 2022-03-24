package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.OrganizationDto;
import io.factorialsystems.msscusers.security.RestTemplateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
public class OrganizationMapstructMapperDecorator implements OrganizationMapstructMapper {

    @Value("${api.host.baseurl}")
    private String apiHost;

    public static final String ACCOUNT_PATH = "/api/v1/account";

    private OrganizationMapstructMapper organizationMapstructMapper;

    @Autowired
    public void setMapstructMapper(OrganizationMapstructMapper organizationMapstructMapper) {
        this.organizationMapstructMapper = organizationMapstructMapper;
    }

    @Override
    public Organization dtoToOrganization(OrganizationDto dto) {
        return organizationMapstructMapper.dtoToOrganization(dto);
    }

    @Override
    public OrganizationDto organizationToDto(Organization organization) {
        OrganizationDto dto = organizationMapstructMapper.organizationToDto(organization);

        if (organization.getWalletId() != null) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());

            Optional<AccountDto> accountDto =
                    Optional.ofNullable(restTemplate.getForObject(apiHost + ACCOUNT_PATH + "/" + organization.getWalletId(), AccountDto.class));

            accountDto.ifPresent(dto::setAccount);
        }

        return dto;
    }

    @Override
    public List<OrganizationDto> listOrganizationToDto(List<Organization> organizations) {
        return organizationMapstructMapper.listOrganizationToDto(organizations);
    }
}
