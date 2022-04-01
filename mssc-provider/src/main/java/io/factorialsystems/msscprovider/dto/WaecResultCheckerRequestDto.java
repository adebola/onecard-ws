package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaecResultCheckerRequestDto {
    private String message;
    private String status;
}
