package io.factorialsystems.msscvoucher.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterDto {
    @Null(message = "Cluster ID cannot be set")
    private String id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal amount;

    @Null(message = "Cluster Balance cannot be set")
    private BigDecimal balance;

    @Null(message = "Activated cannot be set in this class")
    private Boolean activated;

    @Null(message = "Activation date cannot be in this path")
    private Date activationDate;

    @Null(message = "Activated By User cannot be set")
    private String activatedBy;

    private String description;

    @Null(message = "CreatedBy cannot be set")
    private String createdBy;

    @Null(message = "Creation Date cannot be set")
    private Date createdDate;

    @Null(message = "Suspended cannot be set in this path")
    private Boolean suspended;
}
