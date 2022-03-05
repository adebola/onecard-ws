package io.factorialsystems.msscprovider.service.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class FileUploader {

    public UploadFile uploadFile(MultipartFile multipartFile) {

        try {
            return new UploadFile(convertMultipartFileToFile(multipartFile),  multipartFile.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
