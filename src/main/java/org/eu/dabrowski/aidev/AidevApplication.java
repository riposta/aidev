package org.eu.dabrowski.aidev;

import org.eu.dabrowski.aidev.configuration.FeignClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class, FeignClientConfiguration.class})
@EnableRetry
public class AidevApplication {

    public static void main(String[] args) {
        SpringApplication.run(AidevApplication.class, args);
    }

}
