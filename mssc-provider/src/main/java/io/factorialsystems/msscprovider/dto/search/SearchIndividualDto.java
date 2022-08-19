package io.factorialsystems.msscprovider.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndividualDto {
    @NotNull(message = "The parent bulk request Id must be specified")
    private String bulkId;

    private String recipient;
    private String product;
    private Boolean status;
    private Boolean unresolved;
}
