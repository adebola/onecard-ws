package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class BulkRequestExcelWriter {
    private static final String[] HEADERS = { "#", "Recipient", "Product", "Cost (₦)", "status", "reason", "retry" };
    private static final String SHEET = "onecard";

    public ByteArrayInputStream bulkRequestToExcel(List<IndividualRequest> individualRequests, String title) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
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
            for (IndividualRequest individualRequest : individualRequests) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(individualRequest.getRecipient());
                row.createCell(2).setCellValue(individualRequest.getServiceCode());
                row.createCell(3).setCellValue(String.format("%,.2f", individualRequest.getServiceCost()));

                if (individualRequest.getFailed() == null) {
                    row.createCell(4).setCellValue("Unavailable");
                } else if (individualRequest.getFailed()) {
                    row.createCell(4).setCellValue("Failed");
                    row.createCell(5).setCellValue(individualRequest.getFailedMessage());

                    if (individualRequest.getRetryId() == null) {
                        row.createCell(6).setCellValue("Retry Failed");
                    } else  {
                        row.createCell(6).setCellValue("Retry Succeeded");
                    }
                } else {
                    row.createCell(4).setCellValue("Success");
                }

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
