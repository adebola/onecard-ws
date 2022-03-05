package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.dto.IndividualRequestDto;
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
                        case 1:
                            dto.setServiceCode(cell.getStringCellValue());
                            break;

                        case 2:
                            dto.setProductId(cell.getStringCellValue());
                            break;

                        case 3:
                            dto.setServiceCost(BigDecimal.valueOf(cell.getNumericCellValue()));
                            break;

                        case 4:
                            dto.setTelephone(cell.getStringCellValue());
                            break;

                        case 5:
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
                final String errorMessage = String.format("File format error: %s", ex.getMessage());
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            } finally {
                uploadFile.getFile().delete();
            }
        }
    }

