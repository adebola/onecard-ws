package io.factorialsystems.testws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TestwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestwsApplication.class, args);
    }

}
