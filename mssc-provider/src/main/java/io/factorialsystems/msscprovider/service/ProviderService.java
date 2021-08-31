package io.factorialsystems.msscprovider.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscprovider.dao.ProviderCategoryMapper;
import io.factorialsystems.msscprovider.dao.ProviderMapper;
import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.web.mapper.provider.ProviderMapstructMapper;
import io.factorialsystems.msscprovider.web.model.PagedDto;
import io.factorialsystems.msscprovider.web.model.ProviderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderMapper providerMapper;
    private final ProviderCategoryMapper providerCategoryMapper;
    private final ProviderMapstructMapper providerMapstructMapper;

    public PagedDto<ProviderDto> findProviders(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Provider> providers = providerMapper.findAll();

        return createDto(providers);
    }

    public PagedDto<ProviderDto> searchProviders(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Provider> providers = providerMapper.Search(searchString);

        return createDto(providers);
    }

    public ProviderDto findProviderById(Integer id) {
        Provider provider = providerMapper.findById(id);

        if (provider != null) {
            return providerMapstructMapper.providerToProviderDto(provider);
        }

        return null;
    }

    public Integer saveProvider(String userName, ProviderDto providerDto) {

        if (providerMapper.findByCode(providerDto.getCode()) != null) {
            throw new RuntimeException(String.format("Provider Code: %s has already been taken please choose another", providerDto.getCode()));
        }

        Provider provider = providerMapstructMapper.providerDtoToProvider(providerDto);
        provider.setCreatedBy(userName);
        providerMapper.save(provider);

        return provider.getId();
    }

    public void updateProvider(Integer id, ProviderDto dto) {
        Provider provider = providerMapstructMapper.providerDtoToProvider(dto);
        provider.setId(id);

        providerMapper.update(provider);
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
