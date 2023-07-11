package io.factorialsystems.msscprovider.dto;

import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeProviderTransactionsDto {
    List<ProviderBalanceDto> providers;
    List<CombinedRechargeRequest> requests;
}
