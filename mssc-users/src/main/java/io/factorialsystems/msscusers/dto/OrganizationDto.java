package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {
    @Null(message = "id cannot be specified")
    private String id;

    @NotEmpty(message = "Organization Name must be specified")
    private String organizationName;

    @Null(message = "Account cannot be set")
    private AccountDto account;

    private Date createdDate;
    private String createdBy;
}
