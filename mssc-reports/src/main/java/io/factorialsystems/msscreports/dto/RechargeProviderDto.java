package io.factorialsystems.msscreports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProviderDto {

    @Null(message = "Id cannot be set")
    private Integer id;

    @NotNull(message = "Recharge Provider Name must be set")
    private String name;

    @NotNull(message = "Recharge Provider Code must be set")
    private String code;

    private String walletId;
    private String createdBy;
    private Date createdDate;
    private Boolean activated;
    private String activatedBy;
    private Date activationDate;

    private BigDecimal balance;
}
