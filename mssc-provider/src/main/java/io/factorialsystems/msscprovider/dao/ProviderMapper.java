package io.factorialsystems.msscprovider.dao;


import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.Provider;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProviderMapper {
    Page<Provider> findAll();
    Page<Provider> search(String search);
    Provider findById(Integer id);
    Provider findByCode(String code);
    List<Provider> findByCategory(String name);
    void save(Provider provider);
    void update(Provider provider);
}
