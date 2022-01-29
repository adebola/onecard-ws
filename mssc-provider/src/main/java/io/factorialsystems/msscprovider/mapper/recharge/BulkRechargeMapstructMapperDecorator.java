package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.RechargeMapper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.BulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.dto.BulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class BulkRechargeMapstructMapperDecorator implements BulkRechargeMapstructMapper {
    private FactoryProducer producer;
    private RestTemplate restTemplate;
    private RechargeMapper rechargeMapper;
    private ServiceActionMapper serviceActionMapper;
    private BulkRechargeMapstructMapper bulkRechargeMapstructMapper;

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

    @Autowired
    public void setFactoryProducer(FactoryProducer producer) {
        this.producer = producer;
    }
    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Autowired
    public void setRechargeMapper(RechargeMapper rechargeMapper) {
        this.rechargeMapper = rechargeMapper;
    }

    @Autowired
    public void setBulkRechargeMapstructMapper(BulkRechargeMapstructMapper bulkRechargeMapstructMapper) {
        this.bulkRechargeMapstructMapper = bulkRechargeMapstructMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public BulkRechargeRequest rechargeDtoToRecharge(BulkRechargeRequestDto dto) {
        String serviceAction = null;
        String rechargeProviderCode = null;
        BulkRechargeRequest request = bulkRechargeMapstructMapper.rechargeDtoToRecharge(dto);

        // ServiceCode
        String serviceCode = dto.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        List<RechargeFactoryParameters> parameters = rechargeMapper.factory(action.getId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", dto.getServiceCode()));
        }

        AbstractFactory factory = producer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", dto.getServiceCode()));
        }

        request.setServiceId(action.getId());
        request.setServiceCode(action.getServiceCode());
        request.setTotalServiceCost(new BigDecimal(0));

        if (request.getServiceCost() == null) {
            DataEnquiry enquiry = factory.getPlans(serviceAction);
            DataPlanDto planDto = enquiry.getPlan(request.getProductId());
            BigDecimal cost = new BigDecimal(planDto.getPrice());
            request.setServiceCost(cost);
        }

        // GroupId
        if (dto.getGroupId() != null) {
            Integer count =
                    restTemplate.getForObject(baseLocalUrl + "/api/v1/beneficiary/length/" + dto.getGroupId(), Integer.class);

            if (count != null && count > 0) {
                request.setTotalServiceCost(dto.getServiceCost().multiply(new BigDecimal(count)));
                request.setGroupId(dto.getGroupId());
            }
        }

        // Recipient
        if (dto.getRecipients() != null && dto.getRecipients().length > 0) {
            int recipientCount = dto.getRecipients().length;
            double cost = dto.getServiceCost().doubleValue() * recipientCount;
            BigDecimal currentCost = request.getTotalServiceCost();
            request.setTotalServiceCost(currentCost.add(new BigDecimal(cost)));
        }

        return request;
    }
}
