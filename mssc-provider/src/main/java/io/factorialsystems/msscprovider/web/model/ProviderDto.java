package io.factorialsystems.msscprovider.web.model;

import lombok.Data;
import java.util.Date;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Null;
import javax.validation.constraints.NotNull;

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

    private Boolean activated;
    private String status;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    @Null(message = "Activation date will be set automatically")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date activationDate;
}
