package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.RechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RechargeMapper {
    RechargeRequest findById(Integer id);
    void save(RechargeRequest request);
    List<RechargeFactoryParameters> factory(Integer id);
    void closeRequest(Integer id);
}
