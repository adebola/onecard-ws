package io.factorialsystems.msscprovider.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {
    private Integer id;
    private Integer serviceId;
    private String serviceCode;
    private String recipient;
    private String telephone;
    private String productId;
    private BigDecimal serviceCost;
    private String paymentId;
    private String authorizationUrl;
    private String redirectUrl;
    private Timestamp createdDate;
    private Boolean closed;
}
