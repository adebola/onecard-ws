package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.TransactionDto;
import io.factorialsystems.msscreports.dto.TransactionSearchRequestDto;
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
public class TransactionReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_SERVICE_NAME = 1;
    private static final int COLUMN_TRANSACTION_DATE = 2;
    private static final int COLUMN_TRANSACTION_AMOUNT = 3;
    private static final int COLUMN_TRANSACTION_RECIPIENT = 4;
    private static final int COLUMN_TRANSACTION_USER = 5;

    private static final String[] REPORT_USER_HEADERS = {
            "#",
            "Service",
            "Date/Time",
            "Amount",
            "Recipient",
            "User"
    };


    public ByteArrayInputStream transactionToExcel(List<TransactionDto> transactions, TransactionSearchRequestDto dto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("transactions");

            String title;
            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEndDate() != null && dto.getStartDate() != null) {
                title = String.format("Transaction Report for Date Range %s to %s",
                        dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
            } else if (dto.getStartDate() != null) {
                title = String.format("Transaction Report from %s",
                        dateFormat.format(dto.getStartDate()));
            } else {
                title = "Transaction Report";
            }

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);

            cell.setCellValue(title);

            Row headerRow = sheet.createRow(1);
            for (int col = 0; col < REPORT_USER_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(REPORT_USER_HEADERS[col]);
            }

            int rowIdx = 2;

            for (TransactionDto transaction : transactions) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                // ServiceName
                row.createCell(COLUMN_SERVICE_NAME).setCellValue(transaction.getServiceName());

                // DateTime
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(transaction.getTxDate());
                row.createCell(COLUMN_TRANSACTION_DATE).setCellValue(s);

                //  Amount
                row.createCell(COLUMN_TRANSACTION_AMOUNT).setCellValue(transaction.getTxAmount().doubleValue());

                // Recipient
                row.createCell(COLUMN_TRANSACTION_RECIPIENT).setCellValue(transaction.getRecipient());

                // UserName
                row.createCell(COLUMN_TRANSACTION_USER).setCellValue(transaction.getUserName());

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }


}
