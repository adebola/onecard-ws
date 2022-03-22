package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoValidateCableRequestDto {
    private String cardNumber;
    private String cableType;
    private String cableServiceCode;
}
