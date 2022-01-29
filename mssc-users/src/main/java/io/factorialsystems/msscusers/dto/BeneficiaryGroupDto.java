package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryGroupDto {
    private Integer id;

    @NotNull(message = "Group Name must be specified")
    private String groupName;
}
