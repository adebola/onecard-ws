package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SingleRechargeMapper {
    SingleRechargeRequest findById(String id);
    void save(SingleRechargeRequest request);
    List<RechargeFactoryParameters> factory(Integer id);
    void closeRequest(String id);
    void saveRechargeRequests(List<SingleRechargeRequest> requests);
}