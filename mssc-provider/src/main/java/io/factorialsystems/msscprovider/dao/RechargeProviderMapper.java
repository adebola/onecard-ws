package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.domain.RechargeProviderEx;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RechargeProviderMapper {
    Page<RechargeProvider> findAll();
    RechargeProvider findById(Integer id);
    List<RechargeProviderEx> findByServiceId(Integer id);
    void save(RechargeProvider provider);
    void update(RechargeProvider provider);
}
