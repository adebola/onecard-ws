package io.factorialsystems.msscprovider.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.SearchSingleRechargeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SingleRechargeMapper {
    SingleRechargeRequest findById(String id);
    void save(SingleRechargeRequest request);
    List<RechargeFactoryParameters> factory(Integer id);
    void closeRequest(String id);
    void saveRechargeRequests(List<SingleRechargeRequest> requests);
    Page<SingleRechargeRequest> findRequestsByUserId(String id);
    Page<SingleRechargeRequest> search(String search);
    Page<SingleRechargeRequest> adminSearch(SearchSingleRechargeDto dto);
    Boolean setEmailId(Map<String, String> parameters);
}
