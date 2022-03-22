package io.factorialsystems.framegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FrameGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameGatewayApplication.class, args);
    }
}
