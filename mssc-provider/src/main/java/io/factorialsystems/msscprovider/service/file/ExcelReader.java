package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.exception.FileFormatException;
import io.factorialsystems.msscprovider.service.MailService;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelReader {
    private final UploadFile uploadFile;

    public static final int SERVICE_CODE_COLUMN = 1;
    public static final int PRODUCT_ID_COLUMN = 2;
    public static final int SERVICE_COST_COLUMN = 3;
    public static final int TELEPHONE_COLUMN = 4;
    public static final int RECIPIENT_COLUMN = 5;

    public ExcelReader(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    private List<IndividualRequestDto> readXLSContents(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);

        return processSheet(sheet);
    }

    private List<IndividualRequestDto> readXLSXContents(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);

        return processSheet(sheet);
    }

    private List<IndividualRequestDto> processSheet(Sheet sheet) {

        List<IndividualRequestDto> individualRequestDtos = new ArrayList<>();

        int index = 0;

        for (Row row : sheet) {
            if (index > 0) { // Ignore the Header
                IndividualRequestDto dto = new IndividualRequestDto();

                for (Cell cell : row) {
                    switch (cell.getColumnIndex()) {
                        case SERVICE_CODE_COLUMN:
                            dto.setServiceCode(readStringValue(cell, true));
                            break;

                        case PRODUCT_ID_COLUMN:
                            dto.setProductId(readStringValue(cell, false));
                            break;

                        case SERVICE_COST_COLUMN:
                            dto.setServiceCost(readDoubleValue(cell, false));
                            break;

                        case TELEPHONE_COLUMN:
                            dto.setTelephone(readStringValue(cell, false));
                            break;

                        case RECIPIENT_COLUMN:
                            dto.setRecipient(readStringValue(cell, true));
                            break;

                        default:
                            break;
                    }
                }

                individualRequestDtos.add(dto);
            }

            index++;
        }

        return individualRequestDtos;
    }

    public List<IndividualRequestDto> readContents() {

        String extension = FilenameUtils.getExtension(uploadFile.getFileName());

        String errorMessage = "Error";

        try {
            if (extension.equals("xls")) {
                return readXLSContents(uploadFile.getFile());
            } else if (extension.equals("xlsx")) {
                return readXLSXContents(uploadFile.getFile());
            } else {
                errorMessage = String.format("Invalid File extension : (%s)", extension);
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception ex) {
            errorMessage = String.format("File %s format error: %s Uploaded By (%s)", uploadFile.getFileName(), ex.getMessage(), ProviderSecurity.getUserName());
            log.error(errorMessage);
            throw new FileFormatException(errorMessage);
        } finally {
            FileSystemResource fileSystemResource = new FileSystemResource(uploadFile.getFile());
            MailService mailService = ApplicationContextProvider.getBean(MailService.class);

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .body(errorMessage)
                    .to("adeomoboya@gmail.com")
                    .subject("Bulk Recharge File Upload Error")
                    .build();

            mailService.sendMailWithAttachment(fileSystemResource, mailMessageDto);
            // uploadFile.getFile().delete();
        }
    }

    private String readStringValue(Cell cell, boolean mandatory) {
        final String value = cell.getStringCellValue();

        if (value != null && value.trim().length() > 0) {
            return value.trim();
        } else if (!mandatory) {
            return null;
        } else {
            throw new RuntimeException("String value read is either NULL or Empty");
        }
    }

    private BigDecimal readDoubleValue(Cell cell, boolean mandatory) {
        double cost = 0;

        try {
            cost = cell.getNumericCellValue();
        } catch (Exception ex) {
            log.error("Non-Numeric Value Found in Cost, Numeric Value expected");
            log.error(ex.getMessage());

            cost = Double.parseDouble(cell.getStringCellValue());
        }

        if (cost > 0) {
            return BigDecimal.valueOf(cost);
        } else if (!mandatory){
            return null;
        } else {
            throw new RuntimeException("Invalid or No Value in Numeric Field");
        }
    }
}

