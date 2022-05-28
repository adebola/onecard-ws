package io.factorialsystems.msscprovider.domain.rechargerequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortAutoRechargeRequest {
    private String id;
    private String title;
}