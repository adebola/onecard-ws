package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
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
import java.util.Map;

@Component
public class ExcelWriter {
    private static final String[] BULK_HEADERS = { "#", "Recipient", "Product", "Cost (₦)", "status", "reason", "retry" };
    private static final String[] SINGLE_HEADERS = { "#", "Recipient", "Product", "Cost (₦)", "date", "status", "retried", "refunded", "resolved" };
    private static final String[] SINGLE_HEADERS_WITH_USER = { "#", "Recipient", "Product", "Cost (₦)", "date", "status", "retried", "refunded","resolved", "user" };
    private static final String SHEET = "onecard";

    public ByteArrayInputStream bulkRequestToExcel(List<IndividualRequest> individualRequests, String title) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);

            for (int col = 0; col < BULK_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(BULK_HEADERS[col]);
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

    public ByteArrayInputStream singleRequestToExcel(List<SingleRechargeRequest> singleRequests, Map<String, String> entryMap, String title) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);

            if (entryMap == null) {
                for (int col = 0; col < SINGLE_HEADERS.length; col++) {
                    Cell cell2 = headerRow.createCell(col);
                    cell2.setCellValue(SINGLE_HEADERS[col]);
                }
            } else {
                for (int col = 0; col < SINGLE_HEADERS_WITH_USER.length; col++) {
                    Cell cell2 = headerRow.createCell(col);
                    cell2.setCellValue(SINGLE_HEADERS_WITH_USER[col]);
                }
            }

            int rowIdx = 2;
            for (SingleRechargeRequest request : singleRequests) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(request.getRecipient());
                row.createCell(2).setCellValue(request.getServiceCode());
                row.createCell(3).setCellValue(String.format("%,.2f", request.getServiceCost()));

                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());
                row.createCell(4).setCellValue(s);

                if (request.getFailed() == null) {
                    row.createCell(5).setCellValue("Unavailable");
                } else if (request.getFailed()) {
                    row.createCell(5).setCellValue("Failed");

                    if (request.getRetryId() == null) {
                        row.createCell(6).setCellValue("Retry Failed");
                    } else  {
                        row.createCell(6).setCellValue("Retry Succeeded");
                    }

                    if (request.getRefundId() != null) {
                        row.createCell(7).setCellValue("Refunded");
                    }

                    if (request.getResolveId() != null) {
                        row.createCell(8).setCellValue("Resolved");
                    }
                } else {
                    row.createCell(5).setCellValue("Success");
                }

                if (entryMap != null && request.getUserId() != null) {
                    row.createCell(9).setCellValue(entryMap.get(request.getUserId()));
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
