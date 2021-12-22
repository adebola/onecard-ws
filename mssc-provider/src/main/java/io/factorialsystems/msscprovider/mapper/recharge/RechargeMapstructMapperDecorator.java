package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.RechargeRequestDto;
import org.springframework.beans.factory.annotation.Autowired;

public class RechargeMapstructMapperDecorator implements RechargeMapstructMapper {
    private ServiceActionMapper serviceActionMapper;
    private RechargeMapstructMapper rechargeMapstructMapper;

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Autowired
    public void setRechargeMapstructMapper(RechargeMapstructMapper rechargeMapstructMapper) {
        this.rechargeMapstructMapper = rechargeMapstructMapper;
    }

    @Override
    public RechargeRequestDto rechargeToRechargeDto(RechargeRequest request) {
        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    @Override
    public RechargeRequest rechargeDtoToRecharge(RechargeRequestDto dto) {

        RechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);

        String serviceCode = dto.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        request.setServiceId(action.getId());
        request.setServiceCode(action.getServiceCode());

        // We must ensure that for Fixed Priced services such as data Plans, the user must not specify a Price
        // We must also ensure for Variable Priced Services such as airtime the user must specify the Price

        if (action.getServiceCost() != null && request.getServiceCost() != null) {
            throw new RuntimeException(String.format("ServiceCost or Price set for service with fixed cost (%s)", action.getServiceCode()));
        }

        if (action.getServiceCost() != null) {
            request.setServiceCost(action.getServiceCost());
        }

        return request;
    }
}
