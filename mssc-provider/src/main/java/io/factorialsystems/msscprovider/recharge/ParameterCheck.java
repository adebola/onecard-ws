package io.factorialsystems.msscprovider.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.utils.Constants;

public interface ParameterCheck {


    default Boolean check(SingleRechargeRequest request) {
        return request != null &&
                request.getRecipient() != null &&
                request.getServiceCost() != null &&
                request.getServiceCost().compareTo(Constants.MINIMUM_RECHARGE_VALUE) > 0;
    }
}
