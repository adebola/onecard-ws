package io.factorialsystems.msscwallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    @Null(message = "Account Id cannot be set it is generated")
    private String id;

    private String name;
    private String userId;
    private String accountType;
    private Boolean activated;
    private BigDecimal balance;

    @Null(message = "Account creation Date cannot be set it is generated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    @Null(message = "Account createdBy cannot be set it is determined internally")
    private String createdBy;

    @Null(message = "Account Verification cannot be set")
    private Boolean kycVerified;

    @Null(message = "Account Daily Limit cannot be set")
    private BigDecimal dailyLimit;
}
