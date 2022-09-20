package io.factorialsystems.msscprovider.domain.query;

import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class SearchByDate {
    private Timestamp ts;
    private String userId;

    public SearchByDate(Date date) {
        this.ts = new Timestamp(date.getTime());
        userId = ProviderSecurity.getUserId();
    }
}
