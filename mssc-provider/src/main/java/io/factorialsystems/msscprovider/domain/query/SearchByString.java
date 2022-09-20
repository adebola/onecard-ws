package io.factorialsystems.msscprovider.domain.query;

import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.Data;

@Data
public class SearchByString {
    private String search;
    private String userId;

    public SearchByString(String search) {
        this.search = search;
        userId = ProviderSecurity.getUserId();
    }
}
