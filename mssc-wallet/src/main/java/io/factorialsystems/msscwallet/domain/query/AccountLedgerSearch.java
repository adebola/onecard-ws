package io.factorialsystems.msscwallet.domain.query;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccountLedgerSearch {
    private String id;
    private Date date;
}
