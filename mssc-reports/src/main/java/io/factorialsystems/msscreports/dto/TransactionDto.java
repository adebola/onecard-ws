package io.factorialsystems.msscreports.dto;

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
    private String chargeAccountId;
    private Date txDate;
    private BigDecimal txAmount;
    private String recipient;
    private String userId;
    private String userName;
}
