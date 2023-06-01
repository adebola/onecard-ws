package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.FundWalletRequestDto;
import io.factorialsystems.msscreports.dto.WalletReportRequestDto;
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
public class WalletReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_AMOUNT = 1;
    private static final int COLUMN_VERIFIED = 2;
    private static final int COLUMN_DATETIME = 3;
    private static final int COLUMN_TYPE = 4;
    private static final int COLUMN_ACTIONED_BY = 5;
    private static final int COLUMN_USERNAME = 6;

    private static final String[] REPORT_HEADERS = {
            "#",
            "Amount",
            "Verified",
            "Date/Time",
            "Type",
            "Actioned By",
            "User"
    };

    public ByteArrayInputStream walletToExcel(List<FundWalletRequestDto> requests, WalletReportRequestDto dto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("wallet");

            String title;
            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEndDate() != null && dto.getStartDate() != null) {
                title = String.format("Admin Wallet Report for Date Range %s to %s",
                        dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
            } else if (dto.getStartDate() != null) {
                title = String.format("Admin Wallet Report from %s",
                        dateFormat.format(dto.getStartDate()));
            } else {
                title = "Admin Wallet Report";
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

            for (FundWalletRequestDto request : requests) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                //  Amount
                row.createCell(COLUMN_AMOUNT).setCellValue(request.getAmount().doubleValue());

                // Payment Verified
                if (request.getPaymentVerified() == null || !request.getPaymentVerified()) {
                    row.createCell(COLUMN_VERIFIED).setCellValue("False");
                } else {
                    row.createCell(COLUMN_VERIFIED).setCellValue("True");
                }

                // DateTime
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedOn());
                row.createCell(COLUMN_DATETIME).setCellValue(s);

                // Type
                row.createCell(COLUMN_TYPE).setCellValue(request.getType());

                // Actioned By
                row.createCell(COLUMN_ACTIONED_BY).setCellValue(request.getActionedBy());

                // User
                row.createCell(COLUMN_USERNAME).setCellValue(request.getUserName());

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }
}
