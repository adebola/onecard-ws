package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
}
