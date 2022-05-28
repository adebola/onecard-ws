package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoValidateCableResponse {
	private List<ProductItem> product;
	private String smartCardNo;
	private String message;
	private String type;
	private String customerName;
	private String status;
}