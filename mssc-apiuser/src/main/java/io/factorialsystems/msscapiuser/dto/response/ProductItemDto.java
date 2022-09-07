package io.factorialsystems.msscapiuser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemDto {
    private int period;
    private String code;
    private int month;
    private int price;
    private String name;
}

