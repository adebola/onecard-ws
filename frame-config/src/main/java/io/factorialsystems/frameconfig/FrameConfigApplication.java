package io.factorialsystems.frameconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class FrameConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrameConfigApplication.class, args);
    }
}
