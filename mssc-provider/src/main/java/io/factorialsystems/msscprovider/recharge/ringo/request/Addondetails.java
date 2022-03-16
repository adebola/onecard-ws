package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Addondetails{
    private String addoncode; //e.g "ASIADDE36"
    private String name; //e.g "Asian Add-on"
}
