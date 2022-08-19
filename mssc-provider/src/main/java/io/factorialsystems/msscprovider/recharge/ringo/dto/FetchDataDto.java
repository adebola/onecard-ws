package io.factorialsystems.msscprovider.recharge.ringo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchDataDto {
    private String serviceCode;
    private String network;
}
