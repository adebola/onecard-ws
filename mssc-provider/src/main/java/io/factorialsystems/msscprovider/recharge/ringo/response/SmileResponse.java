package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmileResponse {
    private String message;
    private Integer status;
    private List<String> required_fields;

//    private String id;
//    private String account;
//    private BigDecimal price;
//    private String name;
//    private String allowance;
}
