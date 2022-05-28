package io.factorialsystems.msscprovider.recharge.ringo.response.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoWallet {
    private RingoInnerWallet wallet;
    private String status;
    private String message;
}
