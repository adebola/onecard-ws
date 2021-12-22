package io.factorialsystems.msscprovider.recharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class RechargeStatus {
    private String message;
    private HttpStatus status;
}
