package io.factorialsystems.msscprovider.recharge.onecard.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnecardDataPlanResponse {
   private String title;

   @JsonProperty("Plan")
   private List<Plan> plans;

   @Getter
   @Setter
   @ToString
   @JsonIgnoreProperties(ignoreUnknown = true)
   static public class Plan {
       private String id;
       private String code;
       private BigDecimal denomination;

       @JsonProperty("org_denomination")
       private BigDecimal orgDenomination;

       @JsonProperty("currency_amount")
       private BigDecimal currencyAmount;
       private String instructions;
   }
}
