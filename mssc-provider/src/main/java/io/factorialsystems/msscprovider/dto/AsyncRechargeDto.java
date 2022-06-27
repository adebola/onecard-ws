package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsyncRechargeDto {
    private String id;
    private String email;
}
