package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPlanDto {
    private String product_id;
    private String network;
    private String category;
    private String price;
    private String validity;
}
