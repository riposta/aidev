package org.eu.dabrowski.aidev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestAidevApplication {

    public static void main(String[] args) {
        SpringApplication.from(AidevApplication::main).with(TestAidevApplication.class).run(args);
    }

}
