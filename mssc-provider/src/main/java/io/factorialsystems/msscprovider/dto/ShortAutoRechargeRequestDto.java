package io.factorialsystems.msscprovider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortAutoRechargeRequestDto {
    private String id;
    private String title;
    private Date startDate;
    private Date endDate;
    private String recurringType;
}
