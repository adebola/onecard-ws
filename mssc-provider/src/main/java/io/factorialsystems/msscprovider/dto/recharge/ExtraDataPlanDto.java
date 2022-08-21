package io.factorialsystems.msscprovider.dto.recharge;

import io.factorialsystems.msscprovider.recharge.ringo.response.ProductItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExtraDataPlanDto {
   private String customerName;
   private Integer status;
   private String message;
   private String recipient;
   private List<ProductItem> object;
}
