package io.factorialsystems.msscprovider.mapper.provider;

import io.factorialsystems.msscprovider.dao.ProviderCategoryMapper;
import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.domain.ProviderCategory;
import io.factorialsystems.msscprovider.dto.ProviderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ProviderMapstructMapperDecorator implements ProviderMapstructMapper {
    private  ProviderMapstructMapper mapstructMapper;
    private  ProviderCategoryMapper providerCategoryMapper;

    @Autowired
    public void setMapstructMapper(ProviderMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setCategoryMapper(ProviderCategoryMapper providerCategoryMapper) {
        this.providerCategoryMapper = providerCategoryMapper;
    }

    @Override
    public Provider providerDtoToProvider(ProviderDto providerDto) {
        Provider provider = mapstructMapper.providerDtoToProvider(providerDto);

        if (providerDto.getCategory() != null) {
            ProviderCategory category = providerCategoryMapper.findByName(providerDto.getCategory());

            if (category == null) {
                throw new RuntimeException(String.format("Invalid Provider Category: (%s) Please try again with an appropriate one", providerDto.getCategory()));
            }

            provider.setCategoryId(category.getId());
            provider.setCategory(category.getCategoryName());
        }

        return provider;
    }

    @Override
    public ProviderDto providerToProviderDto(Provider provider) {
        return mapstructMapper.providerToProviderDto(provider);
    }

    @Override
    public List<Provider> listProviderDtoToProvider(List<ProviderDto> dto) {
        return mapstructMapper.listProviderDtoToProvider(dto);
    }

    @Override
    public List<ProviderDto> listProviderToProviderDto(List<Provider> providers) {
        return mapstructMapper.listProviderToProviderDto(providers);
    }
}
