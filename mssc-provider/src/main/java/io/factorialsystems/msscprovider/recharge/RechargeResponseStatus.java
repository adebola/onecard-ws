package io.factorialsystems.msscprovider.recharge;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechargeResponseStatus {
    private String message;
    private Boolean status;
    private Object data;
}
