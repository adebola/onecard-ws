package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Integer id;
    private String serviceName;
    private Date txDate;
    private BigDecimal txAmount;
}
