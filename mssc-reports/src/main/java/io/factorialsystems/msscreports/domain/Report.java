package io.factorialsystems.msscreports.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private Integer id;
    private String reportName;
    private String reportFile;
    private String reportDescription;
    private String createdBy;
    private OffsetDateTime createdDate;
}
