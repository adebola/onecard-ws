package io.factorialsystems.msscreports.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscreports.dao.ReportMapper;
import io.factorialsystems.msscreports.domain.Report;
import io.factorialsystems.msscreports.dto.PagedDto;
import io.factorialsystems.msscreports.dto.ReportDto;
import io.factorialsystems.msscreports.mapper.ReportMSMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final AuditService auditService;
    private final ReportMapper reportMapper;
    private final ReportMSMapper reportMSMapper;

    private static final String UPDATE_REPORT = "Report Updated";
    private static final String CREATE_REPORT = "Create Report";

    public PagedDto<ReportDto> findReports(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Report> reports = reportMapper.findAll();

        return createDto(reports);
    }

    private PagedDto<ReportDto> createDto(Page<Report> reports) {
        PagedDto<ReportDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) reports.getTotal());
        pagedDto.setPageNumber(reports.getPageNum());
        pagedDto.setPageSize(reports.getPageSize());
        pagedDto.setPages(reports.getPages());
        pagedDto.setList(reportMSMapper.listReportToReportDto(reports.getResult()));
        return pagedDto;
    }

    public PagedDto<ReportDto> searchReports(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Report> reports = reportMapper.search(searchString);

        return createDto(reports);
    }

    public ReportDto findReportById(Integer id) {
        Report report = reportMapper.findById(id);

        return (report == null) ? null : reportMSMapper.reportToReportDto(report);
    }

    public Integer saveReport(String userName, ReportDto reportDto) {

        Report report = reportMSMapper.reportDtoToReport(reportDto);
        report.setCreatedBy(userName);
        reportMapper.save(report);

        String message = String.format("Created Report %s", report.getReportName());
        auditService.auditEvent(message, CREATE_REPORT);

        return report.getId();
    }

    public void updateReport(Integer id, ReportDto dto) {
        Report report = reportMSMapper.reportDtoToReport(dto);
        report.setId(id);

        String message = String.format("Updated Provider %s", report.getReportName());
        auditService.auditEvent(message, UPDATE_REPORT);

        reportMapper.update(report);
    }

    public ByteArrayInputStream runReport(Integer id) {
        Report report = reportMapper.findById(id);

        try {
            InputStream in = getFileAsIOStream(report.getReportFile());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("reportDetails", "Sample Report");

//            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
//            parameters.put("tableData", dataSource);

            JasperReport jasperReport = JasperCompileManager.compileReport(in);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(print, bos);

            bos.flush();
            bos.close();

            return new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private InputStream getFileAsIOStream(final String fileName) {
        InputStream ios = this.getClass().getResourceAsStream("/jasper/" + fileName);

        if (ios == null) {
            throw new IllegalArgumentException(fileName + " Not Found!!!");
        }

        return ios;
    }
}
