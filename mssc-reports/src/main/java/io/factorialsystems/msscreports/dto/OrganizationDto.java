package io.factorialsystems.msscreports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {
    private String id;
    private String organizationName;
    private Date createdDate;
    private String createdBy;
    private BigDecimal balance;
}
