package io.factorialsystems.msscprovider.dto.recharge;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoQueryRequestDto {
    private BigDecimal amount;
    private String status;
    private String message;
}
