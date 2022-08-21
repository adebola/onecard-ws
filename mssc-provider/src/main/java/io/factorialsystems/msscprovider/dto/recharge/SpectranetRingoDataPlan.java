package io.factorialsystems.msscprovider.dto.recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpectranetRingoDataPlan {
    private String status;
    private String message;
    private String pin_based;
    List<IndividualPlan> product;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndividualPlan {
        BigDecimal price;
    }
}
