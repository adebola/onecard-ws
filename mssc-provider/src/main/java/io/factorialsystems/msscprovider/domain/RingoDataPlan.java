package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoDataPlan {
    private String id;
    private String network;
    private String category;
    private String price;
    private String allowance;
    private String validity;
}
