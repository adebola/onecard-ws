package io.factorialsystems.msscprovider.service.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile {
    private File file;
    private String fileName;
}
