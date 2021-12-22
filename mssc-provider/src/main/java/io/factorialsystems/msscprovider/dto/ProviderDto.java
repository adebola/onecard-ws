package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDto {

    @Null(message = "id cannot be set it will be automatically generated")
    private Integer id;

    @NotNull(message = "To set a category set the Id")
    private String category;

    @NotNull(message = "Provider name cannot be null")
    private String name;

    @NotNull(message = "Provider Code cannot be null")
    private String code;

    @Null(message = "Activated cannot be set")
    private Boolean activated;

    @Null(message = "ActivatedBy cannot be set")
    private String activatedBy;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically")
    private Date createdDate;

    @Null(message = "Activation date will be set automatically")
    private Date activationDate;

    @Null(message = "Suspended cannot be set in this context")
    private Boolean suspended;
}
