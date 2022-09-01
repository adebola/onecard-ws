package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
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
    private static final String[] BULK_INDIVIDUAL_HEADERS = { "#", "Recipient", "Product", "Cost (₦)", "Status", "Reason", "Retry", "Refund", "Resolve", "Results" };
    private static final String[] BULK_HEADERS_WITH_USER = { "#", "Id", "User", "Cost (₦)", "Payment Mode", "Date" };
    private static final String[] BULK_HEADERS = { "#", "Id", "Cost (₦)", "Payment Mode", "Date" };
    private static final String[] SINGLE_HEADERS = { "#", "Recipient", "Product", "Cost (₦)", "Date", "Status", "Retry", "Refund", "Resolve", "Results" };
    private static final String[] SINGLE_HEADERS_WITH_USER = { "#", "Recipient", "Product", "Cost (₦)", "Date", "Status", "Retry", "Refund","Resolve", "User", "Results" };
    private static final String SHEET = "onecard";

    public ByteArrayInputStream bulkRequestToExcel(List<NewBulkRechargeRequest> requests, Map<String, String> entryMap, String title) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);

            if (entryMap != null) {
                for (int col = 0; col < BULK_HEADERS_WITH_USER.length; col++) {
                    Cell cell2 = headerRow.createCell(col);
                    cell2.setCellValue(BULK_HEADERS_WITH_USER[col]);
                }
            } else {
                for (int col = 0; col < BULK_HEADERS.length; col++) {
                    Cell cell2 = headerRow.createCell(col);
                    cell2.setCellValue(BULK_HEADERS[col]);
                }
            }


            int rowIdx = 2;
            for (NewBulkRechargeRequest request : requests) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(request.getId());

                int cellIdx = 2;
                if (entryMap != null) {
                    row.createCell(cellIdx).setCellValue(entryMap.get(request.getUserId()));
                    cellIdx++;
                }

                row.createCell(cellIdx).setCellValue(String.format("%,.2f", request.getTotalServiceCost()));

                cellIdx++;
                row.createCell(cellIdx).setCellValue(request.getPaymentMode());

                cellIdx++;
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());
                row.createCell(cellIdx).setCellValue(s);

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ie) {
            throw new RuntimeException("fail to import data to Excel file: " + ie.getMessage());
        }
    }

    public ByteArrayInputStream bulkIndividualRequestToExcel(List<IndividualRequest> individualRequests, String title) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);

            for (int col = 0; col < BULK_INDIVIDUAL_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(BULK_INDIVIDUAL_HEADERS[col]);
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

                    if (individualRequest.getRefundId() != null) {
                        row.createCell(7).setCellValue("Refunded");
                    }

                    if (individualRequest.getResolveId() != null) {
                        row.createCell(8).setCellValue("Resolved");
                    }
                } else {
                    row.createCell(4).setCellValue("Success");
                }

                if (individualRequest.getResults() != null) {
                    row.createCell(9).setCellValue(individualRequest.getResults());
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

                if (request.getResults() != null) {
                    row.createCell(10).setCellValue(request.getResults());
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
