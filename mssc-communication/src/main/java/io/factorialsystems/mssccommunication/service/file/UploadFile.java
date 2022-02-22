package io.factorialsystems.mssccommunication.service.file;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class UploadFile {
    private File file;
    private String fileName;
}
