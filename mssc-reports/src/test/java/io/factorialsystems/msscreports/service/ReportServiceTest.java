package io.factorialsystems.msscreports.service;

import io.factorialsystems.msscreports.dto.PagedDto;
import io.factorialsystems.msscreports.dto.ReportDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@CommonsLog
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    void findReports() {
        PagedDto<ReportDto> reports = reportService.findReports(1, 20);
        assertNotNull(reports);
        assert(reports.getTotalSize() > 0);
        log.info(reports.getList().get(0));
    }

    @Test
    void searchReports() {
        PagedDto<ReportDto> reports = reportService.searchReports(1, 20, "Or");
        assertNotNull(reports);
        assert(reports.getTotalSize() > 0);
        log.info(reports.getList().get(0));
    }

    @Test
    void findReportById() {
        ReportDto dto = reportService.findReportById(1);
        assertNotNull(dto);
        log.info(dto);
    }

    @Test
    void saveReport() {
        ReportDto dto = new ReportDto();
        dto.setReportFile("onecard.jrxml");
        dto.setReportName("Order Reports");
        dto.setReportDescription("Jesus Christ is the Son of God");

        Integer reportId = reportService.saveReport("adebola", dto);
        ReportDto newReport = reportService.findReportById(reportId);

        assertNotNull(newReport);
        assertEquals(newReport.getReportFile(), dto.getReportFile());
        assertEquals(newReport.getReportName(), dto.getReportName());
        assertEquals(newReport.getReportDescription(), dto.getReportDescription());
    }

    @Test
    void updateReport() {

        String s = "Order Report Updated";

        ReportDto dto = reportService.findReportById(1);
        dto.setReportName(s);
        reportService.updateReport(1, dto);

        ReportDto newReport = reportService.findReportById(1);
        assertNotNull(newReport);
        assertEquals(newReport.getReportName(), dto.getReportName());
    }

    @Test
    void runReport() {
        ByteArrayInputStream in = reportService.runReport(1);
        assertNotNull(in);
        log.info(in.toString());
    }
}
