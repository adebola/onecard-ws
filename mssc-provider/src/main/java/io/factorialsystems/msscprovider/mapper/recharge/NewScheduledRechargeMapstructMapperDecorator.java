package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.NewScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.utils.Constants;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
public class NewScheduledRechargeMapstructMapperDecorator implements NewScheduledRechargeMapstructMapper {
    private PaymentModeHelper paymentModeHelper;
    private IndividualRequestHelper individualRequestHelper;
    private NewScheduledRechargeMapstructMapper mapstructMapper;

    private static final Integer SINGLE_RECHARGE = 1;
    private static final Integer BULK_RECHARGE = 2;

    @Autowired
    public void setIndividualRequestHelper(IndividualRequestHelper individualRequestHelper) {
        this.individualRequestHelper = individualRequestHelper;
    }

    @Autowired
    public void setBulkRechargeMapstructMapper(NewScheduledRechargeMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setPaymentModeHelper(PaymentModeHelper paymentModeHelper) {
        this.paymentModeHelper = paymentModeHelper;
    }

    @Override
    public NewScheduledRechargeRequest rechargeDtoToRecharge(NewScheduledRechargeRequestDto dto) {
        NewScheduledRechargeRequest request = mapstructMapper.rechargeDtoToRecharge(dto);
        request.setUserId(ProviderSecurity.getUserId());
        request.setUserEmail(ProviderSecurity.getEmail());
        String paymentMode = paymentModeHelper.checkPaymentMode(dto.getPaymentMode());

        if (paymentMode.equals(Constants.WALLET_PAY_MODE) && request.getUserId() == null) {
            throw new RuntimeException("You must be logged In to do a Scheduled recharge wallet purchase, please login or choose and alternate payment method");
        }

        request.setPaymentMode(paymentMode);

        // RequestType 'single' or 'bulk'
        if (dto.getRechargeType().equals(Constants.SINGLE_RECHARGE)) {
            request.setRequestType(SINGLE_RECHARGE);
        } else {
            request.setRequestType(BULK_RECHARGE);
        }

        // Scheduled date make sure not in the past or too near
        Date date = dto.getScheduledDate();
        if (!date.after(new Date())) {
            throw new RuntimeException(String.format("Date (%s) is in the past or too near to be scheduled, please run instantly",
                    date.toString()));
        }

        request.setScheduledDate(new Timestamp(date.getTime()));
        BigDecimal totalCost = individualRequestHelper.checkRequests(request.getRecipients());
        request.setTotalServiceCost(totalCost);

        return request;
    }

    @Override
    public NewScheduledRechargeRequestDto rechargeToRechargeDto(NewScheduledRechargeRequest request) {
        return mapstructMapper.rechargeToRechargeDto(request);
    }

    @Override
    public List<NewScheduledRechargeRequestDto> listRechargeToRechargeDto(List<NewScheduledRechargeRequest> requests) {
        return mapstructMapper.listRechargeToRechargeDto(requests);
    }

    @Override
    public NewBulkRechargeRequest ToBulkRechargeRequest(NewScheduledRechargeRequest request) {
        return mapstructMapper.ToBulkRechargeRequest(request);
    }

    private SingleRechargeRequest ToSingleRecharge(NewScheduledRechargeRequest newRequest, IndividualRequest request) {
        return SingleRechargeRequest.builder()
                .userId(newRequest.getUserId())
                .authorizationUrl(newRequest.getAuthorizationUrl())
                .redirectUrl(newRequest.getRedirectUrl())
                .recipient(request.getRecipient())
                .productId(request.getProductId())
                .serviceCode(request.getServiceCode())
                .serviceCost(request.getServiceCost())
                .serviceId(request.getServiceId())
                .build();
    }
}
