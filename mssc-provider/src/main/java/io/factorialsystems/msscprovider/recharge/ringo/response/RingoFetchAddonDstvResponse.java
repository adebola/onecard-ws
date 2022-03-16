package io.factorialsystems.msscprovider.recharge.ringo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoFetchAddonDstvResponse {
	private String message;
	private String status;
}