package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPlanDto {
    private String product_id;
    private String network;
    private String category;
    private String price;
    private String validity;
    private String allowance;
}
