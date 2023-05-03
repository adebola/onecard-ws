package io.factorialsystems.msscreports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsscReportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscReportsApplication.class, args);
    }
}
