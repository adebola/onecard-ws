package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.AuditMessageDto;
import io.factorialsystems.msscreports.dto.AuditSearchDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Component
public class AuditReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_ID = 1;
    public static final int COLUMN_SERVICE = 2;
    public static final int COLUMN_ACTION = 3;
    public static final int COLUMN_USER = 4;
    public static final int COLUMN_DATETIME = 5;
    public static final int COLUMN_DESCRIPTION = 6;

    private static final String[] REPORT_HEADERS = {
            "#",
            "id",
            "Service",
            "Action",
            "User",
            "Date/Time",
            "Description"
    };

    public ByteArrayInputStream generate(List<AuditMessageDto> messages, AuditSearchDto dto)  {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final Sheet sheet = workbook.createSheet("audit");

            String title;
            final Locale locale = new Locale("en", "NG");
            final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEnd() != null && dto.getStart() != null) {
                title = String.format("Admin Audit Report for Date Range %s to %s", dto.getStart(), dto.getEnd());
            } else if (dto.getStart() != null) {
                title = String.format("Admin Audit Report from %s", dto.getStart());
            } else {
                title = "Admin Audit Report";
            }

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);

            cell.setCellValue(title);

            Row headerRow = sheet.createRow(1);
            for (int col = 0; col < REPORT_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(REPORT_HEADERS[col]);
            }

            int rowIdx = 2;

            for (AuditMessageDto auditMessageDto: messages) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                // # Id
                row.createCell(COLUMN_ID).setCellValue(auditMessageDto.getId());

                // Service Name
                row.createCell(COLUMN_SERVICE).setCellValue(auditMessageDto.getServiceName());

                // Service Action
                row.createCell(COLUMN_ACTION).setCellValue(auditMessageDto.getServiceAction());

                // User
                row.createCell(COLUMN_USER).setCellValue(auditMessageDto.getUserName());

                //Date Time
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(auditMessageDto.getCreatedDate());
                row.createCell(COLUMN_DATETIME).setCellValue(s);

                // Description
                row.createCell(COLUMN_DESCRIPTION).setCellValue(auditMessageDto.getUserName());
                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }
}

