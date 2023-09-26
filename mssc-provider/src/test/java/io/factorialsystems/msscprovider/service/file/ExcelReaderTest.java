package io.factorialsystems.msscprovider.service.file;

import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import io.factorialsystems.msscprovider.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Slf4j
class ExcelReaderTest {

    @MockBean
    MailService mailService;

    @Test
    void readContents() throws IOException {
        final String fileName = "/Users/adebola/Downloads/field-staff-depots supervisors.xlsx";
        final Path path = Paths.get(fileName);
        final byte[] bytes = Files.readAllBytes(path);

        MultipartFile file = new MockMultipartFile(
                "field-staff-depots supervisors.xlsx",
                "field-staff-depots supervisors.xlsx", "byte",
                bytes
        );

        FileUploader fileUploader = new FileUploader();
        UploadFile uploadFile = fileUploader.uploadFile(file);

        //given(mailService.sendMailWithAttachment(any(), any(), any(), any())).willReturn("test");

        ExcelReader excelReader = new ExcelReader(mailService);
        final List<IndividualRequestDto> individualRequests = excelReader.readContents(uploadFile);
        log.info("Requests {}", individualRequests);

    }
}