package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaecValidateResponseDto {
    private String message;
    private String status;
    private Boolean pin_based;
    private String type;
    Product product;

    static class Product {
        private Integer availability;
        private Integer price;
        private String name;
        private String allowance;
        private String validity;
        private String code;
    }
}
