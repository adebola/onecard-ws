package io.factorialsystems.msscusers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
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
    private BigDecimal dailyLimit;
    private Boolean kycVerified;

    @Null(message = "Account creation Date cannot be set it is generated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    @Null(message = "Account createdBy cannot be set it is determined internally")
    private String createdBy;
}
