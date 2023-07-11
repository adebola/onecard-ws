package io.factorialsystems.msscprovider.recharge.onecard.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestOnecardAirtimeDto {
    private Integer product_id;
    private String mobile;
    private Integer amount;
    private String params;
    private String plan_params;
}
