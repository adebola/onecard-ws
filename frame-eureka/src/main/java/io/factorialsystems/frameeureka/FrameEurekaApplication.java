package io.factorialsystems.frameeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class FrameEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameEurekaApplication.class, args);
    }

}
