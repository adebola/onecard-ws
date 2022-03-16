package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoPayWithOutAddonDstvResponse {
	private String message;
	private String status;
}