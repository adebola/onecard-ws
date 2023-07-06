package io.factorialsystems.msscreports.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscreports.dao.ReportMapper;
import io.factorialsystems.msscreports.domain.Report;
import io.factorialsystems.msscreports.dto.*;
import io.factorialsystems.msscreports.external.client.AccountClient;
import io.factorialsystems.msscreports.external.client.AuditClient;
import io.factorialsystems.msscreports.external.client.ProviderClient;
import io.factorialsystems.msscreports.external.client.UserClient;
import io.factorialsystems.msscreports.generate.excel.AuditReportGenerator;
import io.factorialsystems.msscreports.generate.excel.RechargeReportGenerator;
import io.factorialsystems.msscreports.generate.excel.UserReportGenerator;
import io.factorialsystems.msscreports.generate.excel.WalletReportGenerator;
import io.factorialsystems.msscreports.mapper.ReportMSMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserClient userClient;
    private final AuditClient auditClient;
    private final AuditService auditService;
    private final ReportMapper reportMapper;
    private final AccountClient accountClient;
    private final ReportMSMapper reportMSMapper;
    private final ProviderClient providerClient;
    private final UserReportGenerator userReportGenerator;
    private final AuditReportGenerator auditReportGenerator;
    private final WalletReportGenerator walletReportGenerator;
    private final RechargeReportGenerator rechargeReportGenerator;

    private static final String UPDATE_REPORT = "Report Updated";
    private static final String CREATE_REPORT = "Create Report";

    public PagedDto<ReportDto> findReports(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Report> reports = reportMapper.findAll();

        return createDto(reports);
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

    @SneakyThrows
    public InputStreamResource runRechargeReport(RechargeReportRequestDto dto) {
        CombinedRechargeList combinedRechargeList = providerClient.getRechargeReport(dto);
        return new InputStreamResource(rechargeReportGenerator.rechargeToExcel(combinedRechargeList.getRequests(), dto));
    }

    public InputStreamResource runWalletReport(WalletReportRequestDto dto) {

        if (dto.getType().equals("user")) {
            List<FundWalletRequestDto> requests = accountClient.getWalletFunding(dto);
            return new InputStreamResource(walletReportGenerator.walletToExcel(requests, dto));
        } else if (dto.getType().equals("provider")) {
            // Get Reports From Provider
            //providerClient.getProviderTransactions(dto);
            return null;
        }

        return null;
    }

    public InputStreamResource runAuditReport(AuditSearchDto auditSearchDto) {
        List<AuditMessageDto> auditMessages = auditClient.getAudits(auditSearchDto);
        log.info("Run Audit Report AuditMessages dto {}, size {}", auditSearchDto, auditMessages.size());
        return new InputStreamResource(auditReportGenerator.generate(auditMessages, auditSearchDto));
    }

    public InputStreamResource runUserReport() {
        List<SimpleUserDto> users = userClient.getAllUsers();
        log.info("Run User Report size {}", users.size());
        return new InputStreamResource(userReportGenerator.reportToExcel(users));
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
}
