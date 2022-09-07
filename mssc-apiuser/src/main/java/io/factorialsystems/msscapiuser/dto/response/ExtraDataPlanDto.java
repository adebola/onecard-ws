package io.factorialsystems.msscapiuser.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraDataPlanDto {
   private String customerName;
   private Integer status;
   private String message;
   private String recipient;
   private List<ProductItemDto> object;
}
