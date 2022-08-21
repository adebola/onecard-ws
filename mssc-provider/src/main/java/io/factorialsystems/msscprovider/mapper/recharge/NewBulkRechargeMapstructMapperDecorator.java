package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeRequestDto;
import io.factorialsystems.msscprovider.utils.K;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class NewBulkRechargeMapstructMapperDecorator implements NewBulkRechargeMapstructMapper {
    private PaymentModeHelper paymentModeHelper;
    private IndividualRequestHelper individualRequestHelper;
    private NewBulkRechargeMapstructMapper newBulkRechargeMapstructMapper;

    @Autowired
    public void setPaymentModeHelper(PaymentModeHelper paymentModeHelper) {
        this.paymentModeHelper = paymentModeHelper;
    }

    @Autowired
    public void setIndividualRequestHelper(IndividualRequestHelper individualRequestHelper) {
        this.individualRequestHelper = individualRequestHelper;
    }

    @Autowired
    public void setNewBulkRechargeMapstructMapper(NewBulkRechargeMapstructMapper newBulkRechargeMapstructMapper) {
        this.newBulkRechargeMapstructMapper = newBulkRechargeMapstructMapper;
    }

    @Override
    public NewBulkRechargeRequest rechargeDtoToRecharge(NewBulkRechargeRequestDto dto) {
        NewBulkRechargeRequest request = newBulkRechargeMapstructMapper.rechargeDtoToRecharge(dto);
        request.setUserId(K.getUserId());

        request.setPaymentMode(paymentModeHelper.checkPaymentMode(dto.getPaymentMode()));
        BigDecimal totalCost = individualRequestHelper.checkRequests(request.getRecipients());
        request.setTotalServiceCost(totalCost);

        return request;
    }

    @Override
    public NewBulkRechargeRequestDto rechargeToRechargDto(NewBulkRechargeRequest request) {
        return newBulkRechargeMapstructMapper.rechargeToRechargDto(request);
    }

    @Override
    public List<NewBulkRechargeRequestDto> listRechargeToRechargDto(List<NewBulkRechargeRequest> requests) {
        return newBulkRechargeMapstructMapper.listRechargeToRechargDto(requests);
    }

    @Override
    public IndividualRequest individualDtoToIndividual(IndividualRequestDto dto) {
        return newBulkRechargeMapstructMapper.individualDtoToIndividual(dto);
    }

    @Override
    public List<IndividualRequest> listIndividualDtoToIndividual(List<IndividualRequestDto> dtos) {
        return newBulkRechargeMapstructMapper.listIndividualDtoToIndividual(dtos);
    }

    @Override
    public IndividualRequestDto individualToIndividualDto(IndividualRequest request) {
        return newBulkRechargeMapstructMapper.individualToIndividualDto(request);
    }

    @Override
    public List<IndividualRequestDto> listIndividualToIndividualDto(List<IndividualRequest> requests) {
        return newBulkRechargeMapstructMapper.listIndividualToIndividualDto(requests);
    }
}
