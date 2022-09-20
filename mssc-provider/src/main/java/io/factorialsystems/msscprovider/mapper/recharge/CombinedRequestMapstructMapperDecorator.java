package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class CombinedRequestMapstructMapperDecorator implements CombinedRequestMapstructMapper {
    private CombinedRequestMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(CombinedRequestMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Override
    public CombinedRechargeRequest singleToCombined(SingleRechargeRequest request) {
        CombinedRechargeRequest rechargeRequest = mapstructMapper.singleToCombined(request);
        rechargeRequest.setRechargeType("Single");

        return rechargeRequest;
    }

    @Override
    public CombinedRechargeRequest individualToCombined(IndividualRequest individualRequest) {
        CombinedRechargeRequest rechargeRequest = mapstructMapper.individualToCombined(individualRequest);
        rechargeRequest.setRechargeType("Bulk");

        return rechargeRequest;
    }
}
