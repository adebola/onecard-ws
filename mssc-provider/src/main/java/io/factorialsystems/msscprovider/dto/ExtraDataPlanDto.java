package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtraDataPlanDto {
   private String customerName;
   private Integer status;
   private String message;
   private String recipient;
   private Object object;
}
