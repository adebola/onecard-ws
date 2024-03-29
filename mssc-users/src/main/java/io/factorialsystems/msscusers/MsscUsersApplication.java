package io.factorialsystems.msscusers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsscUsersApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscUsersApplication.class, args);
    }
}
