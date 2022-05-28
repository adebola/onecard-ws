package io.factorialsystems.msscprovider.recharge.ringo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoPayCableResponse {
	private String message;
	private String status;
	private String type;
	private String date;

	@JsonProperty("transref")
	private String transRef;
	
	@JsonProperty("package")
	private String cablePackage;
	
	private BigDecimal amount;
	private BigDecimal amountCharged;
}