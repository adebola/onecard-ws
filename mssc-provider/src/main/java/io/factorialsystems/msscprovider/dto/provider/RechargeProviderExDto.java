package io.factorialsystems.msscprovider.dto.provider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProviderExDto {
    private Integer id;
    private String name;
    private String code;
    private String weight;
}
