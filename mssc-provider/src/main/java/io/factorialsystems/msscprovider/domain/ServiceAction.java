package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAction {
    private Integer id;
    private String serviceName;
    private BigDecimal serviceCost;
    private Integer providerId;
    private String providerCode;
    private String providerName;
    private String createdBy;
    private Date createdDate;
    private Boolean activated;
}
