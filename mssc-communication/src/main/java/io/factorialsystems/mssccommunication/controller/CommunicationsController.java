package io.factorialsystems.mssccommunication.controller;

import io.factorialsystems.mssccommunication.service.s3.AmazonS3BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communication")
public class CommunicationsController {
    private final AmazonS3BucketService amazonS3BucketService;

    @PostMapping("/uploadfile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return this.amazonS3BucketService.uploadFile(file);
    }
}
