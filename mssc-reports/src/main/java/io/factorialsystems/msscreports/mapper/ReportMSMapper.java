package io.factorialsystems.msscreports.mapper;

import io.factorialsystems.msscreports.domain.Report;
import io.factorialsystems.msscreports.dto.ReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface ReportMSMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "reportName", target = "reportName"),
            @Mapping(source = "reportFile", target = "reportFile"),
            @Mapping(source = "reportDescription", target = "reportDescription"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    Report reportDtoToReport(ReportDto reportDto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "reportName", target = "reportName"),
            @Mapping(source = "reportFile", target = "reportFile"),
            @Mapping(source = "reportDescription", target = "reportDescription"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    ReportDto reportToReportDto(Report report);

    List<Report> listReportDtoToReport(List<ReportDto> dtos);
    List<ReportDto> listReportToReportDto(List<Report> reports);
}
