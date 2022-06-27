package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoIndividualRequest {
    private Integer id;
    private String autoRequestId;
    private Integer serviceId;
    private String serviceCode;
    private String productId;
    private BigDecimal serviceCost;
    private String telephone;
    private String recipient;
}
