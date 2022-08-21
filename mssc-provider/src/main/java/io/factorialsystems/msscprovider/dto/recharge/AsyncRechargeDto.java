package io.factorialsystems.msscprovider.dto.recharge;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AsyncRechargeDto {
    private String id;
    private String email;
    private String name;
    private BigDecimal balance;
}
