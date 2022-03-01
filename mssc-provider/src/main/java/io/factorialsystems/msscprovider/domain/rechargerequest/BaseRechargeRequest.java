package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRechargeRequest {
    private String id;
    private String userId;
    private String redirectUrl;
    private String authorizationUrl;
    private String paymentMode;
    private String paymentId;
}
