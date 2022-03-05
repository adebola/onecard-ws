package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkRechargeRequestDto {
    @NotEmpty(message = "Code must be specified")
    private String serviceCode;

    private Integer groupId;
    private String[] recipients;

    private String productId;
    private String redirectUrl;

    @Digits(integer = 9, fraction = 2)
    private BigDecimal serviceCost;

    private String paymentId;
    private String paymentMode;
}
