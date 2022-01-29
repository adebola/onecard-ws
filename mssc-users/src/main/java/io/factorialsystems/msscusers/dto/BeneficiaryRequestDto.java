package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryRequestDto {

    @NotNull(message = "Please specify Beneficiary Group Id")
    private Integer groupId;

    @NotNull(message = "Please specify Beneficiary Id")
    private Integer beneficiaryId;
}
