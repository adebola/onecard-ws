package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.OrganizationDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class CorporateUserReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_ID = 1;
    private static final int COLUMN_CREATED = 2;
    private static final int COLUMN_ORGANIZATION_NAME = 3;
    private static final int COLUMN_BALANCE = 4;

    private static final String[] REPORT_HEADERS = {
            "#",
            "ID",
            "Created",
            "Organization Name",
            "Balance",
    };

    public ByteArrayInputStream reportToExcel(List<OrganizationDto> organizations) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("organizations");

            final String title = "Admin Corporate Report";

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

            for (OrganizationDto organization : organizations) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                //  Id
                row.createCell(COLUMN_ID).setCellValue(organization.getId());

                //  Date
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(organization.getCreatedDate());
                row.createCell(COLUMN_CREATED).setCellValue(s);

                // Organization Name
                row.createCell(COLUMN_ORGANIZATION_NAME).setCellValue(organization.getOrganizationName());

                // Balance
                if (organization.getBalance() == null) {
                    row.createCell(COLUMN_BALANCE).setCellValue(0);
                } else {
                    row.createCell(COLUMN_BALANCE).setCellValue(organization.getBalance().doubleValue());
                }

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }
}
