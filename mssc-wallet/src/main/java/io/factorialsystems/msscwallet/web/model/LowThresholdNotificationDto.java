package io.factorialsystems.msscwallet.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowThresholdNotificationDto {
    private List<String> users;
}
