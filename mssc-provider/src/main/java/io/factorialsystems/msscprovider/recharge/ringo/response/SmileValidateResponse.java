package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmileValidateResponse {
    private String message;
    private Integer status;
    private String customerName;
    private String customer_id;
    private String type;
}


