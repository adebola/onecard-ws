package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoValidateCableRequestDto {
    @NotEmpty(message = "cardNumber must be specified")
    private String cardNumber;

    private String serviceCode;
}
