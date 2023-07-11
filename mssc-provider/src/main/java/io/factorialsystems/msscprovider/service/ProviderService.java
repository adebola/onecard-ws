package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ProviderMapper;
import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.dto.PagedDto;
import io.factorialsystems.msscprovider.dto.provider.ProviderDto;
import io.factorialsystems.msscprovider.mapper.provider.ProviderMapstructMapper;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final AuditService auditService;
    private final ProviderMapper providerMapper;
    private final ProviderMapstructMapper providerMapstructMapper;

    private static final String EVENT_CREATE_PROVIDER = "Create Provider";
    private static final String EVENT_UPDATE_PROVIDER = "Update Provider";
    private static final String EVENT_SUSPEND_PROVIDER = "Suspend provider";
    private static final String EVENT_ACTIVATE_PROVIDER = "Activate Provider";

    public PagedDto<ProviderDto> findProviders(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Provider> providers = providerMapper.findAll();

        return createDto(providers);
    }

    public PagedDto<ProviderDto> searchProviders(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Provider> providers = providerMapper.search(searchString);

        return createDto(providers);
    }

    public ProviderDto findProviderById(Integer id) {
        return providerMapstructMapper.providerToProviderDto(providerMapper.findById(id));
    }

    public List<ProviderDto> findByCategory(String category) {
        return providerMapstructMapper.listProviderToProviderDto(providerMapper.findByCategory(category));
    }

    public Integer saveProvider(String userName, ProviderDto providerDto) {

        if (providerMapper.findByCode(providerDto.getCode()) != null) {
            throw new RuntimeException(String.format("Provider Code: %s has already been taken please choose another", providerDto.getCode()));
        }

        Provider provider = providerMapstructMapper.providerDtoToProvider(providerDto);
        provider.setCreatedBy(userName);
        providerMapper.save(provider);

        String message = String.format("Created Provider %s by %s", provider.getName(), ProviderSecurity.getUserName());
        auditService.auditEvent(message,EVENT_CREATE_PROVIDER);

        return provider.getId();
    }

    public void updateProvider(Integer id, ProviderDto dto) {
        Provider provider = providerMapstructMapper.providerDtoToProvider(dto);
        provider.setId(id);

        String message = String.format("Updated Provider %s by %s", provider.getName(), ProviderSecurity.getUserName());
        auditService.auditEvent(message, EVENT_UPDATE_PROVIDER);

        providerMapper.update(provider);
    }

    public ProviderDto activateProvider(Integer id) {
        Provider provider = providerMapper.findById(id);

        if (provider != null) {
            provider.setActivated(true);
            provider.setActivationDate(new Timestamp(System.currentTimeMillis()));
            provider.setActivatedBy(ProviderSecurity.getUserName());

            providerMapper.update(provider);

            return providerMapstructMapper.providerToProviderDto(provider);
        }

        return null;
    }

    public ProviderDto suspendProvider(Integer id) {
        Provider provider = providerMapper.findById(id);

        if (provider != null) {
            provider.setSuspended(true);
            providerMapper.update(provider);

            return providerMapstructMapper.providerToProviderDto(provider);
        }

        return null;
    }

    public ProviderDto unsuspendProvider(Integer id) {
        Provider provider = providerMapper.findById(id);

        if (provider != null) {
            provider.setSuspended(false);
            providerMapper.update(provider);

            return providerMapstructMapper.providerToProviderDto(provider);
        }

        return null;
    }

    private PagedDto<ProviderDto> createDto(Page<Provider> providers) {
        PagedDto<ProviderDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) providers.getTotal());
        pagedDto.setPageNumber(providers.getPageNum());
        pagedDto.setPageSize(providers.getPageSize());
        pagedDto.setPages(providers.getPages());
        pagedDto.setList(providerMapstructMapper.listProviderToProviderDto(providers.getResult()));
        return pagedDto;
    }
}
