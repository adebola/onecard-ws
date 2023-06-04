package io.factorialsystems.mssccommunication.controller;

import io.factorialsystems.mssccommunication.service.s3.AmazonS3BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/upload")
public class UploadController {
    private final AmazonS3BucketService amazonS3BucketService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return this.amazonS3BucketService.uploadFile(file);
    }
}
