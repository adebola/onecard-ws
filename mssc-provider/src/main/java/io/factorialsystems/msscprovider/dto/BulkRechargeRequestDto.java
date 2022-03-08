package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkRechargeRequestDto {
    @NotNull(message = "Code must be specified")
    private String serviceCode;

    private Integer groupId;
    private String[] recipients;

    private String productId;
    private String redirectUrl;
    private BigDecimal serviceCost;
    private String paymentId;
    private String paymentMode;
}
