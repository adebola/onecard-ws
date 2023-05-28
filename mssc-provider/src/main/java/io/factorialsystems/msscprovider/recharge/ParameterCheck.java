package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;

public interface ParameterCheck {
    default Boolean check(SingleRechargeRequest request) {
        return request != null && request.getRecipient() != null && request.getServiceCost() != null;
    }
}
