package io.factorialsystems.msscprovider.dao;


import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceActionMapper {
    public Page<ServiceAction> findByProviderCode(String code);
    public ServiceAction findById(Integer id);
    public void save(ServiceAction action);
    public void update(ServiceAction action);
}
