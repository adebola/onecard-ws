package io.factorialsystems.msscwallet.service.file;

import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.dto.DateRangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelWriter {
    private static final String[] HEADERS = { "#", "Service", "Date", "Amount (₦)", "Recipient"};
    private static final String SHEET = "transactions";

    public ByteArrayInputStream WriteTransactions(List<Transaction> transactions, DateRangeDto dto) {
        String title = null;

        if (dto.getFrom() == null && dto.getTo() == null) {
            title = "All Transactions";
        } else if (dto.getTo() == null && dto.getFrom() != null) {
            final String from = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(dto.getFrom());
            title = String.format("All Transactions from %s", from);
        } else if (dto.getTo() != null && dto.getFrom() == null) {
            final String to = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(dto.getFrom());
            title = String.format("All transactions to %s", to);
        } else {
            final String from = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(dto.getFrom());
            final String to = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(dto.getFrom());

            title = String.format("Transactions Between %s and %s", from, to);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(HEADERS[col]);
            }

            int rowIdx = 2;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(transaction.getServiceName());
                row.createCell(2).setCellValue(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(transaction.getTxDate()));
                row.createCell(3).setCellValue(String.format("%,.2f", transaction.getTxAmount()));
                row.createCell(4).setCellValue(transaction.getRecipient());

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
