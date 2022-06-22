package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundWalletRequestDto {
    @NotNull(message = "Amount to be funded must be specified")
    private BigDecimal amount;

    @Null(message = "Status cannot be specified")
    private Integer status;

    @Null(message = "Payment Verified cannot be set")
    private Boolean paymentVerified;

    @Null(message = "Message cannot be specified")
    private String message;

    @Null(message = "Message cannot be specified")
    private Date createdOn;

    @Null(message = "Closed cannot be specified")
    private Boolean closed;

    private String redirectUrl;
}
