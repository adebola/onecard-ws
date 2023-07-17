package io.factorialsystems.msscreports.generate.excel;

import io.factorialsystems.msscreports.dto.FundWalletRequestDto;
import io.factorialsystems.msscreports.dto.RechargeProviderExpenditure;
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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WalletReportGenerator {
    private static final int COLUMN_LINE_NUMBER = 0;
    private static final int COLUMN_USER_AMOUNT = 1;
    private static final int COLUMN_USER_VERIFIED = 2;
    private static final int COLUMN_USER_DATETIME = 3;
    private static final int COLUMN_USER_TYPE = 4;
    private static final int COLUMN_USER_ACTIONED_BY = 5;
    private static final int COLUMN_USER_USERNAME = 6;

    private static final int COLUMN_SHORT_PROVIDER_PROVIDER = 1;
    private static final int COLUMN_SHORT_PROVIDER_EXPENDITURE = 2;
    private static final int COLUMN_SHORT_PROVIDER_DATE = 3;

    private static final String[] REPORT_USER_HEADERS = {
            "#",
            "Amount",
            "Verified",
            "Date/Time",
            "Type",
            "Actioned By",
            "User"
    };

    private static final String[] REPORT_SHORT_PROVIDER_HEADERS = {
            "#",
            "Provider",
            "Expenditure"
    };

    private static final String[] REPORT_LONG_PROVIDER_HEADERS = {
            "#",
            "Provider",
            "Expenditure",
            "Date"
    };

    public ByteArrayInputStream userWalletToExcel(List<FundWalletRequestDto> requests, WalletReportRequestDto dto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("wallet");

            String title;
            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEndDate() != null && dto.getStartDate() != null) {
                title = String.format("Admin User Wallet Report for Date Range %s to %s",
                        dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
            } else if (dto.getStartDate() != null) {
                title = String.format("Admin User Wallet Report from %s",
                        dateFormat.format(dto.getStartDate()));
            } else {
                title = "Admin User Wallet Report";
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

            for (FundWalletRequestDto request : requests) {
                Row row = sheet.createRow(rowIdx);

                // # Number
                row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

                //  Amount
                row.createCell(COLUMN_USER_AMOUNT).setCellValue(request.getAmount().doubleValue());

                // Payment Verified
                if (request.getPaymentVerified() == null || !request.getPaymentVerified()) {
                    row.createCell(COLUMN_USER_VERIFIED).setCellValue("False");
                } else {
                    row.createCell(COLUMN_USER_VERIFIED).setCellValue("True");
                }

                // DateTime
                String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedOn());
                row.createCell(COLUMN_USER_DATETIME).setCellValue(s);

                // Type
                row.createCell(COLUMN_USER_TYPE).setCellValue(request.getType());

                // Actioned By
                row.createCell(COLUMN_USER_ACTIONED_BY).setCellValue(request.getActionedBy());

                // User
                row.createCell(COLUMN_USER_USERNAME).setCellValue(request.getUserName());

                rowIdx++;
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }


    public ByteArrayInputStream providerShortWalletToExcel(List<RechargeProviderExpenditure> requests, WalletReportRequestDto dto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("short-provider-wallet");

            String title;
            Locale locale = new Locale("en", "NG");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

            if (dto.getEndDate() != null && dto.getStartDate() != null) {
                title = String.format("Admin Short Provider Wallet Report for Date Range %s to %s",
                        dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
            } else if (dto.getStartDate() != null) {
                title = String.format("Admin Short Wallet Report from %s",
                        dateFormat.format(dto.getStartDate()));
            } else {
                title = "Admin Short Wallet Provider Report";
            }

            // Title
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);

            cell.setCellValue(title);

            Row headerRow = sheet.createRow(1);
            for (int col = 0; col < REPORT_SHORT_PROVIDER_HEADERS.length; col++) {
                Cell cell2 = headerRow.createCell(col);
                cell2.setCellValue(REPORT_SHORT_PROVIDER_HEADERS[col]);
            }

            int rowIdx = 2;

            writeProviderRows(sheet, rowIdx, requests);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }

    public ByteArrayInputStream providerLongWalletToExcel(List<RechargeProviderExpenditure> requests, WalletReportRequestDto dto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            final List<String> providers = requests.stream()
                    .map(RechargeProviderExpenditure::getProvider).distinct()
                    .collect(Collectors.toList());

            providers.forEach(provider -> {
                Sheet sheet = workbook.createSheet(provider);

                String title;
                Locale locale = new Locale("en", "NG");
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

                if (dto.getEndDate() != null && dto.getStartDate() != null) {
                    title = String.format("Admin Provider Wallet Report for %s for Date Range %s to %s",
                            provider, dateFormat.format(dto.getStartDate()), dateFormat.format(dto.getStartDate()));
                } else if (dto.getStartDate() != null) {
                    title = String.format("Admin Provider Wallet Report for %s from %s",
                            provider, dateFormat.format(dto.getStartDate()));
                } else {
                    title = "Admin Provider Wallet Provider Report";
                }

                // Title
                Row titleRow = sheet.createRow(0);
                Cell cell = titleRow.createCell(0);

                cell.setCellValue(title);

                Row headerRow = sheet.createRow(1);
                for (int col = 0; col < REPORT_LONG_PROVIDER_HEADERS.length; col++) {
                    Cell cell2 = headerRow.createCell(col);
                    cell2.setCellValue(REPORT_LONG_PROVIDER_HEADERS[col]);
                }

                int rowIdx = 2;

                List<RechargeProviderExpenditure> filteredRequests = requests.stream()
                        .filter(r -> r.getProvider().equals(provider)).
                        sorted(new Comparator<RechargeProviderExpenditure>() {
                            @Override
                            public int compare(RechargeProviderExpenditure o1, RechargeProviderExpenditure o2) {
                                return o1.getDay().compareTo(o2.getDay());
                            }
                        }).collect(Collectors.toList());

                writeProviderRows(sheet, rowIdx, filteredRequests);
            });

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("fail to import data to Excel file: " + ioe.getMessage());
        }
    }

    private void writeProviderRows(Sheet sheet, int rowIdx, List<RechargeProviderExpenditure> filteredRequests) {
        for (RechargeProviderExpenditure request : filteredRequests) {
            Row row = sheet.createRow(rowIdx);

            // # Number
            row.createCell(COLUMN_LINE_NUMBER).setCellValue(rowIdx - 1);

            // Provider
            row.createCell(COLUMN_SHORT_PROVIDER_PROVIDER).setCellValue(request.getProvider());

            //  Expenditure
            row.createCell(COLUMN_SHORT_PROVIDER_EXPENDITURE).setCellValue(new BigDecimal(request.getExpenditure()).doubleValue());

            if (request.getDay() != null) {
                String s = new SimpleDateFormat("dd-MMM-yyyy").format(request.getDay());
                row.createCell(COLUMN_SHORT_PROVIDER_DATE).setCellValue(s);
            }

            rowIdx++;
        }
    }
}
