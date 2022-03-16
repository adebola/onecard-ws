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
	private String hasAddon; //e.g "True";
	private String period; //e.g "1"
	private String smartCardNo;
	private String code; //e.g "ACSSE36"
	private String serviceCode; //e.g "P-TV"
	private String name; //e.g "DStv Access"
	private String type; //e.g "DSTV"
	private String requestId;
	private Addondetails addondetails;
}
