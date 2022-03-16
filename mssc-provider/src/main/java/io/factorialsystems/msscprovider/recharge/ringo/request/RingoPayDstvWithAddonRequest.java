package io.factorialsystems.msscprovider.recharge.ringo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RingoPayDstvWithAddonRequest {
	private String hasAddon = "True";
	private String period; //e.g "1"
	private String smartCardNo;
	private String code; //e.g "ACSSE36"
	private String serviceCode; //e.g "P-TV"
	private String name; //e.g "DStv Access"
	private String type = "DSTV";
	private String requestId;
	private Addondetails addondetails;
}
