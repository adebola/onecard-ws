package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeRequestDto {
    private String paymentMode;
    private String redirectUrl;
    private List<IndividualRequestDto> recipients;
}
