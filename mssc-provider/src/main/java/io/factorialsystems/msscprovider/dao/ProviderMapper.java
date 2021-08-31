package io.factorialsystems.msscprovider.dao;


import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.Provider;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProviderMapper {
    Page<Provider> findAll();
    Page<Provider> Search(String search);
    Provider findById(Integer id);
    Provider findByCode(String code);
    void save(Provider provider);
    void update(Provider provider);
}
