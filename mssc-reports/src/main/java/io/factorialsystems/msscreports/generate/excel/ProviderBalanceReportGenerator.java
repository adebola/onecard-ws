package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.RechargeProviderDto;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class ProviderBalanceReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_PROVIDER_NAME = 1;
    private static final int COLUMN_PROVIDER_BALANCE = 2;

    private static final String[] REPORT_PROVIDER_HEADERS = {
            "#",
            "Name",
            "Balance"
    };


    public ByteArrayInputStream providerBalancesToExcel(List<RechargeProviderDto> requests) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("balances");

            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
            final String title = String.format("Provider Balances as at %s", dateFormat.format(new Date()));

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);

            cell.setCellValue(title);

            Row headerRow = sheet.createRow(1);
            for (int col = 0; col < REPORT_PROVIDER_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(REPORT_PROVIDER_HEADERS[col]);
            }

            int rowIdx = 2;

            for (RechargeProviderDto request : requests) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                // Name
                row.createCell(COLUMN_PROVIDER_NAME).setCellValue(request.getName());

                //  Balance
                if (request.getBalance() != null) {
                    row.createCell(COLUMN_PROVIDER_BALANCE).setCellValue(request.getBalance().doubleValue());
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
