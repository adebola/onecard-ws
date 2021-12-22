package io.factorialsystems.msscwallet.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Null(message = "You cannot set Transaction Id it is auto-generated")
    private Integer id;

    private String accountId;
    private String counterpartyName;
    private String serviceName;

    @Null(message = "Transaction Date is Auto-generated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date txDate;

    private BigDecimal txAmount;
    private String txNarrative;
    private String txStatus;
}
