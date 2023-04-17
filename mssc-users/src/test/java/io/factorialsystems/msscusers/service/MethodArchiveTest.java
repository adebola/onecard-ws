package io.factorialsystems.msscusers.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
class MethodArchiveTest {

    @Test
    public void saveImageFile() throws IOException {

        MultipartFile file = new MockMultipartFile("channelLogo.jpeg", "channelLogo.jpeg", "byte",
                "ChannelLogo".getBytes());
        final String fileName = "./" + file.getOriginalFilename();
        log.info(String.format("Received File %s Sending to UploadServer", fileName));


            byte[] bytes = file.getBytes();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(fileName)
            );

            bufferedOutputStream.write(bytes);
            bufferedOutputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//                headers.setBearerAuth(Objects.requireNonNull(K.getAccessToken()));

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(fileName));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            // restTemplate.getInterceptors().add(new RestTemplateInterceptor());

            //restTemplate.postForObject("api/v1/upload2", requestEntity, String.class);
    }
}
