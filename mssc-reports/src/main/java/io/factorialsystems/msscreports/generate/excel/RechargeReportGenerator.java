package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.CombinedRechargeRequest;
import io.factorialsystems.msscreports.dto.RechargeReportRequestDto;
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
public class RechargeReportGenerator {
    public static final int COLUMN_LINE_NUMBER = 0;
    public static final int COLUMN_ID = 1;
    public static final int COLUMN_PARENT_ID = 2;
    public static final int COLUMN_USER = 3;
    public static final int COLUMN_SERVICE = 4;
    public static final int COLUMN_PRODUCT = 5;
    public static final int COLUMN_COST = 6;
    public static final int COLUMN_FAILED = 7;
    public static final int COLUMN_CREATED = 8;
    public static final int COLUMN_REFUNDED = 9;
    public static final int COLUMN_TYPE = 10;
    public static final int COLUMN_RESULTS = 11;
    private static final int COLUMN_PAYMENT_MODE = 12;

    public static final String[] REPORT_HEADERS = {
            "#",
            "id",
            "Parent Id",
            "User",
            "Service",
            "Product",
            "Transaction Amt",
            "status",
            "Date/Time",
            "refunded",
            "type",
            "results",
            "payment"
    };

    public ByteArrayInputStream rechargeToExcel(List<CombinedRechargeRequest> requests, RechargeReportRequestDto dto)  {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("recharge");

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);

            String title;
            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEndDate() != null && dto.getStartDate() != null) {
                title = String.format("Admin Recharge Report for Date Range %s to %s",
                        dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
            } else if (dto.getStartDate() != null) {
                title = String.format("Admin Recharge Report from  %s",
                        dateFormat.format(dto.getStartDate()));
            } else {
                title = "Admin Recharge Report";
            }

            cell.setCellValue(title);

            // Header
            Row headerRow = sheet.createRow(1);
            for (int col = 0; col < REPORT_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(REPORT_HEADERS[col]);
            }

            int rowIdx = 2;

            for (CombinedRechargeRequest request : requests) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                // # Id
                row.createCell(COLUMN_ID).setCellValue(request.getId());

                // # Parent Id
                row.createCell(COLUMN_PARENT_ID).setCellValue(request.getParentId());

                // User
                if (request.getUserName() != null) {
                    row.createCell(COLUMN_USER).setCellValue(request.getUserName());
                }

                // # Service
                row.createCell(COLUMN_SERVICE).setCellValue(request.getServiceCode());

                // # Product
                if (request.getProductId() != null) {
                    row.createCell(COLUMN_PRODUCT).setCellValue(request.getProductId());
                }

                // # Cost
                row.createCell(COLUMN_COST).setCellValue(request.getServiceCost().doubleValue());

                // # Failed, Success, Refunded
                if (request.getFailed() == null) {
                    row.createCell(COLUMN_FAILED).setCellValue("Unavailable");
                } else if (request.getFailed()) {
                    row.createCell(COLUMN_FAILED).setCellValue("Failed");

                    if (request.getRefundId() != null) {
                        row.createCell(COLUMN_REFUNDED).setCellValue("Refunded");
                    }

                } else {
                    row.createCell(COLUMN_FAILED).setCellValue("Success");
                }

                // # Created
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());
                row.createCell(COLUMN_CREATED).setCellValue(s);

                // # Recharge Type Bulk or Single
                row.createCell(COLUMN_TYPE).setCellValue(request.getRechargeType());

                if (request.getResults() != null) {
                    row.createCell(COLUMN_RESULTS).setCellValue(request.getResults());
                }

                if (request.getPaymentMode() != null) {
                    row.createCell(COLUMN_PAYMENT_MODE).setCellValue(request.getPaymentMode());
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
