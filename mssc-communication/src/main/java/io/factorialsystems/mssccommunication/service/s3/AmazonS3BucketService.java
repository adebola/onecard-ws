package io.factorialsystems.mssccommunication.service.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.factorialsystems.mssccommunication.service.file.FileService;
import io.factorialsystems.mssccommunication.service.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3BucketService {

    private AmazonS3 amazonS3;
    private final FileService fileService;

    @Value("${aws.endpoint}")
    private String endpointUrl;

    @Value("${aws.bucket}")
    private String bucketName;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {

        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.EU_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) {

        UploadFile uploadFile = fileService.uploadFile(multipartFile);
        uploadFileToBucket(uploadFile.getFileName(), uploadFile.getFile());

        return  endpointUrl + "/" + bucketName + "/" + uploadFile.getFileName();
    }

    private void uploadFileToBucket(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromBucket(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Deletion Successful";
    }
}
