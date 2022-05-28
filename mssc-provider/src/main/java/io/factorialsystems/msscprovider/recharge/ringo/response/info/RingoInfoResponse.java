package io.factorialsystems.msscprovider.recharge.ringo.response.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoInfoResponse {
    private String status;
    private RingoCustomer customer;
    private RingoWallet wallet;
}
