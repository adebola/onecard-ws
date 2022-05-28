package io.factorialsystems.msscprovider.recharge.ringo.response.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoInnerWallet {
    private String code;
    private String balance;
    private String bonus_balance;
    private String commission_balance;
}
