package io.factorialsystems.msscprovider.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class FileUploader {

    public UploadFile uploadFile(MultipartFile multipartFile) {

        try {
            return new UploadFile(convertMultipartFileToFile(multipartFile),  multipartFile.getOriginalFilename());
        } catch (Exception e) {
            final String errorMessage = String.format("Error Uploading File %s, Contact Support", multipartFile.getOriginalFilename());
            log.error(errorMessage);
            log.error("Original Exception Message : {}", e.getMessage());
            throw new RuntimeException(errorMessage);
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
