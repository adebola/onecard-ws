package io.factorialsystems.msscusers.mapper;

import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.OrganizationDto;
import io.factorialsystems.msscusers.external.client.AccountClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class OrganizationMapstructMapperDecorator implements OrganizationMapstructMapper {
    private AccountClient accountClient;
    private OrganizationMapstructMapper organizationMapstructMapper;

    @Autowired
    public void setAccountClient(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Autowired
    public void setOrganizationMapstructMapper(OrganizationMapstructMapper organizationMapstructMapper) {
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
            AccountDto account = accountClient.getAccount(organization.getWalletId());
            if (account != null) dto.setAccount(account);
        }

        return dto;
    }

    @Override
    public List<OrganizationDto> listOrganizationToDto(List<Organization> organizations) {
        return organizationMapstructMapper.listOrganizationToDto(organizations);
    }
}
