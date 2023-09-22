package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.UserDto;
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
public class UserReportGenerator {

    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_ID = 1;
    private static final int COLUMN_CREATED = 2;
    private static final int COLUMN_USERNAME = 3;
    private static final int COLUMN_FIRSTNAME = 4;
    private static final int COLUMN_LASTNAME = 5;
    private static final int COLUMN_EMAIL = 6;
    private static final int COLUMN_BALANCE = 7;
    private static final int COLUMN_MAIL_VERIFIED = 8;

    private static final String[] REPORT_HEADERS = {
            "#",
            "ID",
            "Created",
            "UserName",
            "FirstName",
            "LastName",
            "E-Mail",
            "Balance",
            "E-Mail Verified"
    };

    public ByteArrayInputStream reportToExcel(List<UserDto> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("users");

            final String title = "Admin User Report";

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

            for (UserDto user : users) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                //  Id
                row.createCell(COLUMN_ID).setCellValue(user.getId());

                //  Date
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(user.getCreatedDate());
                row.createCell(COLUMN_CREATED).setCellValue(s);

                // UserName
                row.createCell(COLUMN_USERNAME).setCellValue(user.getUsername());

                // First Name
                row.createCell(COLUMN_FIRSTNAME).setCellValue(user.getFirstName());

                // Last Name
                row.createCell(COLUMN_LASTNAME).setCellValue(user.getLastName());

                // E-Mail
                row.createCell(COLUMN_EMAIL).setCellValue(user.getEmail());

                // Balance
                if (user.getBalance() == null) {
                    row.createCell(COLUMN_BALANCE).setCellValue(0);
                } else {
                    row.createCell(COLUMN_BALANCE).setCellValue(user.getBalance().doubleValue());
                }

                if (user.getEmailVerified() == null || !user.getEmailVerified()) {
                    row.createCell(COLUMN_MAIL_VERIFIED).setCellValue(false);
                } else {
                    row.createCell(COLUMN_MAIL_VERIFIED).setCellValue(true);
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
