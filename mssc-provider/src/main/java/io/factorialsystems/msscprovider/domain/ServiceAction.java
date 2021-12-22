package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAction {
    private Integer id;
    private String serviceCode;
    private String serviceName;
    private BigDecimal serviceCost;
    private Integer providerId;
    private String providerCode;
    private String providerName;
    private String createdBy;
    private Date createdDate;
    private Boolean activated;
    private String activatedBy;
    private Timestamp activationDate;
    private Integer actionId;
    private String actionName;
    private Boolean suspended;
}
