package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDto {

    @Null(message = "Beneficiary Id cannot be set")
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;

    @NotNull(message = "Beneficiary Telephone Number must be specified")
    private String telephone;
}
