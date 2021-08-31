package io.factorialsystems.msscprovider.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String serviceName;

    @NotNull(message = "Service Cost must be specified")
    private BigDecimal serviceCost;

    @NotNull(message = "Service Provider must be specified")
    private String providerCode;

    private String providerName;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    private Boolean activated;
}
