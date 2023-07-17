package io.factorialsystems.msscprovider.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class RechargeProviderExpenditure {
    private String provider;
    private String expenditure;
    private Date day;
}
