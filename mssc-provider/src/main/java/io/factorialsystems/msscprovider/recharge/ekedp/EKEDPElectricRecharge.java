package io.factorialsystems.msscprovider.recharge.ekedp;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.wsdl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class EKEDPElectricRecharge implements Recharge, ParameterCheck, Balance {
    private final ServicesImpl services;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        OrderDetails orderDetails = services.performRecharge(request);
        if(orderDetails==null)
            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_GATEWAY)
                    .message("Something went wrong, please try later")
                    .build();

        return RechargeStatus.builder()
                .status(HttpStatus.OK)
                .message("Recharge Successful")
                .body(orderDetails)
                .build();
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null &&
                request.getRecipient() != null &&
                request.getServiceCost() != null &&
                request.getAccountType() != null;
    }

    @Override
    public BigDecimal getBalance() {
        return services.getBalance();
    }
}