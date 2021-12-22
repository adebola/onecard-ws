package io.factorialsystems.msscreports.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    @Null(message = "Report Id cannot be set")
    private Integer id;

    @NotNull(message = "The report name MUST be specified")
    private String reportName;

    @NotNull(message = "The report file MUST be specified")
    private String reportFile;

    @NotNull(message = "The report description MUST be specified")
    private String reportDescription;

    @Null(message = "You cannot set the CreatedBy Field")
    private String createdBy;

    @Null(message = "Creation date will be set automatically and cannot be set")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;
}
