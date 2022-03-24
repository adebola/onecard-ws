package io.factorialsystems.msscusers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscusers.config.JMSConfig;
import io.factorialsystems.msscusers.dao.OrganizationMapper;
import io.factorialsystems.msscusers.domain.Organization;
import io.factorialsystems.msscusers.dto.*;
import io.factorialsystems.msscusers.mapper.OrganizationMapstructMapper;
import io.factorialsystems.msscusers.security.RestTemplateInterceptor;
import io.factorialsystems.msscusers.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final OrganizationMapper organizationMapper;
    private final OrganizationMapstructMapper organizationMapstructMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    public PagedDto<OrganizationDto> findAll(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(organizationMapper.findAll());
    }

    public OrganizationDto findById(String id) {
        return organizationMapstructMapper.organizationToDto(organizationMapper.findById(id));
    }

    public PagedDto<OrganizationDto> search(String searchString, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(organizationMapper.search(searchString));
    }

    public OrganizationDto save(OrganizationDto dto) {
        final String id = UUID.randomUUID().toString();

        Organization organization = organizationMapstructMapper.dtoToOrganization(dto);

        CreateAccountDto accountDto = CreateAccountDto.builder()
                .userName(dto.getOrganizationName())
                .userId(id)
                .accountType(K.ACCOUNT_TYPE_CORPORATE)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        AccountDto newAccount =
                restTemplate.postForObject(baseUrl + "/api/v1/account/create", accountDto, AccountDto.class);

        if (newAccount == null || newAccount.getId() == null) {
            String message = String.format("Error creating Account for Organization (%s)", dto.getOrganizationName());
            log.error(message);
            throw new RuntimeException(message);
        }

        organization.setId(id);
        organization.setWalletId(newAccount.getId());
        organization.setCreatedBy(K.getUserName());
        organizationMapper.save(organization);
        dto.setId(id);

        return dto;
    }

    public OrganizationDto update(String id, OrganizationDto dto) {
        Organization organization = organizationMapstructMapper.dtoToOrganization(dto);
        organization.setId(id);
        organizationMapper.update(organization);
        return organizationMapstructMapper.organizationToDto(organizationMapper.findById(id));
    }

    public void delete(String id) {
        Organization organization = organizationMapper.findById(id);

        if (organization == null) {
            throw new RuntimeException("Organization not Available for deletion");
        }

        Integer count = organizationMapper.findUserCount(id);

        if (count != null && count == 0) {
            log.info("Deleting Organization {}", organization.getId());
            organizationMapper.delete(id);

            DeleteAccountDto dto = DeleteAccountDto.builder()
                    .id(organization.getWalletId())
                    .deletedBy(K.getPreferredUserName())
                    .build();

            // Delete the account
            try {
                jmsTemplate.convertAndSend(JMSConfig.DELETE_ACCOUNT_QUEUE, objectMapper.writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return;
        }

        throw new RuntimeException("Organization cannot be deleted, it has associated Users");
    }

    private PagedDto<OrganizationDto> createDto(Page<Organization> organizations) {
        PagedDto<OrganizationDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) organizations.getTotal());
        pagedDto.setPageNumber(organizations.getPageNum());
        pagedDto.setPageSize(organizations.getPageSize());
        pagedDto.setPages(organizations.getPages());

        pagedDto.setList(organizationMapstructMapper.listOrganizationToDto(organizations.getResult()));
        return pagedDto;
    }
}
