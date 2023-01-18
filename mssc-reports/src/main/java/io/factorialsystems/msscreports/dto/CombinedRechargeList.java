package io.factorialsystems.msscreports.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinedRechargeList {
    private List<CombinedRechargeRequest> requests;
}
