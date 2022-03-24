package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewBulkRechargeRequestDto {
    private String paymentMode;
    private String redirectUrl;

    @NotEmpty
    private List<@Valid IndividualRequestDto> recipients;
}
