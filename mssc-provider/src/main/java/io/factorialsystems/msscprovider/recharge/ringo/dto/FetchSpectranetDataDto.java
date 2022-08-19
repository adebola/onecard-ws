package io.factorialsystems.msscprovider.recharge.ringo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchSpectranetDataDto {
    private String serviceCode;
    private String type;
}
