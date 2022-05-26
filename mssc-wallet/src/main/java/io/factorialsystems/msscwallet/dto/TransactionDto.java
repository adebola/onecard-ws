package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    @Null(message = "Transaction Id cannot be set")
    private Integer id;

    private String serviceName;

    @Null(message = "Charge Account Id cannot be set")
    private String chargeAccountId;

    private Date txDate;
    private BigDecimal txAmount;
    private String recipient;
}
