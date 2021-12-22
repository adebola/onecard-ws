package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.ProviderCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProviderCategoryMapper {
    Page<ProviderCategory> findAll();
    Page<ProviderCategory> search(String search);
    ProviderCategory findById(Integer id);
    ProviderCategory findByName(String name);
    void save(ProviderCategory providerCategory);
    void update(ProviderCategory providerCategory);
}
