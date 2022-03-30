package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RingoPayCableRequest {
	private String serviceCode;
	private String type;
	private String smartCardNo;
	private String code;
	private String period;
	private String hasAddon;
	private String name;
	private String request_id;
}
