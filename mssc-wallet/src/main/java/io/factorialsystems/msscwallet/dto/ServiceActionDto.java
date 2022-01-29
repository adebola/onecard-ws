package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceActionDto {

    @Null(message = "Category Id will be set automatically")
    private Integer id;

    @NotNull(message = "Service Name must be specified")
    private String serviceCode;

    @NotNull(message = "Service Name must be specified")
    private String serviceName;

    private BigDecimal serviceCost;

    @NotNull(message = "Service Provider must be specified")
    private String providerCode;

    @Null(message = "Provider name cannot be specified")
    private String providerName;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically")
    private Date createdDate;

    @Null(message = "Activated cannot be specified")
    private Boolean activated;

    @Null(message = "ActivatedBy cannot be specified")
    private String activatedBy;

    @Null(message = "Activation Date cannot be specified")
    private Date activationDate;

    @NotNull
    private String actionName;

    @Null(message = "Suspended cannot be specified")
    private Boolean suspended;
}
