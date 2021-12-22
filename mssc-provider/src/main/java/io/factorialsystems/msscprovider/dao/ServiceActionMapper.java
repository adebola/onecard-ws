package io.factorialsystems.msscprovider.dao;


import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.Action;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceActionMapper {
    Page<ServiceAction> findByProviderId(Integer id);
    Page<ServiceAction> findByProviderCode(String code);
    ServiceAction findById(Integer id);
    ServiceAction findByCode(String serviceCode);
    void save(ServiceAction action);
    void update(ServiceAction action);
    List<Action> findAllActions();
    void addRechargeProvider(Map<String, Integer> map);
    void removeRechargeProvider(Map<String, Integer> map);
}
