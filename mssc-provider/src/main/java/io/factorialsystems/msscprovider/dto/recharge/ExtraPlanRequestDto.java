package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraPlanRequestDto {
    @NotEmpty(message = "Recipient must be specified")
    private String recipient;

    @NotEmpty(message = "Service Code must be specified")
    private String serviceCode;

    private String accountType;
}
