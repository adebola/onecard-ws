package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.exception.FileFormatException;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
                            dto.setServiceCode(cell.getStringCellValue());
                            break;

                        case PRODUCT_ID_COLUMN:
                            final String productId = cell.getStringCellValue();

                            if (productId != null && productId.trim().length() > 0) {
                                dto.setProductId(productId.trim());
                            }

                            break;

                        case SERVICE_COST_COLUMN:
                            final double cost = cell.getNumericCellValue();

                            if(cost > 0) {
                                dto.setServiceCost(BigDecimal.valueOf(cost));
                            }

                            break;

                        case TELEPHONE_COLUMN:
                            final String telephone = cell.getStringCellValue();

                            if (telephone != null && telephone.trim().length() > 0) {
                                dto.setTelephone(telephone.trim());
                            }

                            break;

                        case RECIPIENT_COLUMN:
                            dto.setRecipient(cell.getStringCellValue());
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

        try {

            if (extension.equals("xls")) {
                return readXLSContents(uploadFile.getFile());
            } else if (extension.equals("xlsx")) {
                return readXLSXContents(uploadFile.getFile());
            } else {
                final String errorMessage = String.format("Invalid File extension : (%s)", extension);
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (Exception ex) {
                final String errorMessage = String.format("File %s format error: %s Uploaded By (%s)", uploadFile.getFileName(), ex.getMessage(), K.getUserName());
                log.error(errorMessage);
                throw new FileFormatException(errorMessage);
            } finally {
                // uploadFile.getFile().delete();
            }
        }
    }

