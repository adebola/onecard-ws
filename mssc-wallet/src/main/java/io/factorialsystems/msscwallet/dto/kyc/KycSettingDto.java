package io.factorialsystems.msscwallet.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycSettingDto {
    private String enable;
    private String firstName;
    private String lastName;
    private String telephone;
    private BigDecimal userLimit;
    private BigDecimal corporateLimit;
}