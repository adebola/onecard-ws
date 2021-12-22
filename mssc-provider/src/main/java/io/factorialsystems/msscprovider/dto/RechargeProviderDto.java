package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.Timestamp;
import java.util.Date;

@Data
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
}
