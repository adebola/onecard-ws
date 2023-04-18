package io.factorialsystems.msscapiuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsscApiuserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscApiuserApplication.class, args);
    }
}
